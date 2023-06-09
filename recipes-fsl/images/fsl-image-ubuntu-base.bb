# A simple image with a Ubuntu rootfs
#
# Note that we have a tight dependency to ubuntu-base
# and that we cannot just install arbitrary Yocto packages to avoid
# rootfs pollution or destruction.
PV = "${@d.getVar('PREFERRED_VERSION_ubuntu-base', True) or '1.0'}"

IMAGE_LINGUAS = ""
IMAGE_INSTALL = ""
DEPENDS += "shadow-native"
inherit image
export PACKAGE_INSTALL = "${IMAGE_INSTALL}"

inherit nativeaptinstall

# We must permit network access during rootfs creation for apt to work
do_rootfs[network] = "1"

APTGET_CHROOT_DIR = "${IMAGE_ROOTFS}"
APTGET_SKIP_UPGRADE = "1"

ROOTFS_POSTPROCESS_COMMAND:append = "do_aptget_update; do_update_host; do_update_dns; do_enable_network_manager; do_systemd_service_fixup; do_getty_fixup; "

# This must be added first as it provides the foundation for
# subsequent modifications to the rootfs
IMAGE_INSTALL += "\
	ubuntu-base \
	ubuntu-base-dev \
	ubuntu-base-dbg \
	ubuntu-base-doc \
"

# Without the kernel and modules, we can't really use the Linux
IMAGE_INSTALL += "\
	kernel-devicetree \
	kernel-image \
	kernel-modules \
"

# We want to have an itb to boot from in the /boot directory to be flexible
# about U-Boot behavior
IMAGE_INSTALL += "\
   linux-kernelitb-norootfs-image \
"

APTGET_EXTRA_PACKAGES_SERVICES_DISABLED += "\
	network-manager \
"
APTGET_EXTRA_PACKAGES += "\
	console-setup locales \
	mc htop \
\
	apt git vim \
	ethtool wget ftp iputils-ping lrzsz \
	net-tools \
"
APTGET_EXTRA_SOURCE_PACKAGES += "\
	iproute2 \
"

# Add user bluebox with password bluebox and default shell bash
USER_SHELL_BASH = "/bin/bash"
USER_PASSWD_BLUEBOX = "SNSRrmx0usMiI"
APTGET_ADD_USERS = "bluebox:${USER_PASSWD_BLUEBOX}:${USER_SHELL_BASH}"

HOST_NAME = "ubuntu-${MACHINE_ARCH}"

##############################################################################
# NOTE: We cannot install arbitrary Yocto packages as they will
# conflict with the content of the prebuilt Ubuntu rootfs and pull
# in dependencies that may break the rootfs.
# Any package addition needs to be carefully evaluated with respect
# to the final image that we build.
##############################################################################

# Minimum support for LS2 specific elements.
IMAGE_INSTALL:append:fsl-lsch3 = " \
    mc-utils-image \
    restool \
"

# We want easy installation of the BlueBox image to the target
# Supported for any Layerscape Gen3 except LX2
DEPLOYSCRIPTS ?= "bbdeployscripts"
DEPLOYSCRIPTS:lx2160a = ""
DEPENDS:append:fsl-lsch3 = " \
    ${DEPLOYSCRIPTS} \
"

require ${@bb.utils.contains('DISTRO_FEATURES', 'pfe', 'recipes-fsl/images/fsl-image-pfe.inc', '', d)}

# User/group hacking needs to be done in the chroot context as the
# Yocto tools don't match the Ubuntu tools.
# As it turns out, this isn't really a hard necessity to do apparently
# on the Ubuntu rootfs because everything is already in order, but
# we leave the code in just in case.
ROOTFS_POSTPROCESS_COMMAND:remove = "systemd_create_users;"
fakeroot do_aptget_user_update_preinstall:append () {
	aptget_update_presetvars;

	for conffile in ${APTGET_CHROOT_DIR}/usr/lib/sysusers.d/*.conf; do
		[ -e $conffile ] || continue
		grep -v "^#" $conffile | sed -e '/^$/d' | while read fullline; do
		# Magic for quoted args like the comment field
		# We use xargs to deal with quoted fields but turn the
		# result into separate lines so that we can get to the
		# individual args. We pad for non-existing tails.
		arglines=$(printf %s "$fullline"|xargs printf "%s\n")
		arglines=$(printf "%s\n\n\n\n\n\n<EOF>\n" "$arglines")
		type=$(    printf %s "$arglines"|head -n 1|tail -n 1)
		name=$(    printf %s "$arglines"|head -n 2|tail -n 1)
		id=$(      printf %s "$arglines"|head -n 3|tail -n 1)
		comment=$( printf %s "$arglines"|head -n 4|tail -n 1)
		homedir=$( printf %s "$arglines"|head -n 5|tail -n 1)
		shell=$(   printf %s "$arglines"|head -n 6|tail -n 1)
		[ "$comment" = "-" ] && comment=""
		[ "$homedir" = "-" ] && homedir=""
		[ "$shell" = "-" ] && shell=""
		if [ "$type" = "u" ]; then
			user=`echo "$id"|cut -d: -f1`
			group=`echo "$id"|cut -s -d: -f2-`
			if [ -z "$shell" ]; then
				if [ "$id" = "0" ]; then
					shell="/bin/sh"
				else
					shell="/sbin/nologin"
				fi
			fi
			useradd_params="--shell $shell"
			[ "$user" != "-" ] && useradd_params="$useradd_params --uid $user"
			[ ! -z "$group" ] && useradd_params="$useradd_params --gid $group"
			[ ! -z "$homedir" ] && useradd_params="$useradd_params --home $homedir"
			useradd_params="$useradd_params --system $name"
			if [ -z "$comment" ]; then
				chroot "${APTGET_CHROOT_DIR}" ${root_prefix}/sbin/useradd $useradd_params || true
			else
				chroot "${APTGET_CHROOT_DIR}" ${root_prefix}/sbin/useradd --comment "$comment" $useradd_params || true
			fi
		elif [ "$type" = "g" ]; then
			groupadd_params=""
			[ "$id" != "-" ] && groupadd_params="$groupadd_params --gid $id"
			groupadd_params="$groupadd_params --system $name"
			chroot "${APTGET_CHROOT_DIR}" ${root_prefix}/sbin/groupadd $groupadd_params || true
		elif [ "$type" = "m" ]; then
			group=$id
			chroot "${APTGET_CHROOT_DIR}" ${root_prefix}/sbin/groupadd --system $group || true
			chroot "${APTGET_CHROOT_DIR}" ${root_prefix}/sbin/useradd --shell /sbin/nologin --system $name --no-user-group || true
			chroot "${APTGET_CHROOT_DIR}" ${root_prefix}/sbin/usermod -a -G $group $name
		fi
		done
	done
}

fakeroot do_update_host() {
	echo >"${APTGET_CHROOT_DIR}/etc/hostname" "${HOST_NAME}"

	echo  >"${APTGET_CHROOT_DIR}/etc/hosts" "127.0.0.1 localhost"
	echo >>"${APTGET_CHROOT_DIR}/etc/hosts" "127.0.1.1 ${HOST_NAME}"
	echo >>"${APTGET_CHROOT_DIR}/etc/hosts" ""
	echo >>"${APTGET_CHROOT_DIR}/etc/hosts" "# The following lines are desirable for IPv6 capable hosts"
	echo >>"${APTGET_CHROOT_DIR}/etc/hosts" "::1 ip6-localhost ip6-loopback"
	echo >>"${APTGET_CHROOT_DIR}/etc/hosts" "fe00::0 ip6-localnet"
	echo >>"${APTGET_CHROOT_DIR}/etc/hosts" "ff00::0 ip6-mcastprefix"
	echo >>"${APTGET_CHROOT_DIR}/etc/hosts" "ff02::1 ip6-allnodes"
	echo >>"${APTGET_CHROOT_DIR}/etc/hosts" "ff02::2 ip6-allrouters"
	echo >>"${APTGET_CHROOT_DIR}/etc/hosts" "ff02::3 ip6-allhosts"
}

fakeroot do_update_dns() {
	if [ ! -L "${APTGET_CHROOT_DIR}/etc/resolv.conf" ]; then
		if [ -e "${APTGET_CHROOT_DIR}/etc/resolveconf" ]; then
			mkdir -p "/run/resolveconf"
			if [ -f "${APTGET_CHROOT_DIR}/etc/resolv.conf" ]; then
				mv -f "${APTGET_CHROOT_DIR}/etc/resolv.conf" "/run/resolveconf/resolv.conf"
			fi
			ln -sf  "/run/resolveconf/resolv.conf" "${APTGET_CHROOT_DIR}/etc/resolv.conf"
		elif [ -e "${APTGET_CHROOT_DIR}/etc/dhcp/dhclient-enter-hooks.d/resolved" ]; then
			mkdir -p "/run/systemd/resolve"
			if [ -f "${APTGET_CHROOT_DIR}/etc/resolv.conf" ]; then
				mv -f "${APTGET_CHROOT_DIR}/etc/resolv.conf" "/run/systemd/resolve/resolv.conf"
			fi
			ln -sf  "/run/systemd/resolve/resolv.conf" "${APTGET_CHROOT_DIR}/etc/resolv.conf"
		else
			touch "${APTGET_CHROOT_DIR}/etc/resolv.conf"
		fi
	fi
}

fakeroot do_enable_network_manager() {
	# In bionic, but not in xenial. We want all [network] interfaces to be managed
	# so that we do not have to mess with interface files individually
	if [ -e "${APTGET_CHROOT_DIR}/usr/lib/NetworkManager/conf.d/10-globally-managed-devices.conf" ]; then
		sed -i -E "s/^unmanaged-devices\=\*/unmanaged-devices\=none/g" "${APTGET_CHROOT_DIR}/usr/lib/NetworkManager/conf.d/10-globally-managed-devices.conf"
	fi
}

fakeroot do_systemd_service_fixup() {
	# The systemd preset will enable @ services but doesn't know
	# about their corresponding device name, so we get bad links
	# in the systemd config referencing nothing. This confuses the
	# startup and leads to unnecessary waits
	find ${APTGET_CHROOT_DIR}${sysconfdir}/systemd -name "*@.service" -execdir rm {} \;
	ln -sf ../lib/systemd/systemd  ${APTGET_CHROOT_DIR}/sbin/init
}

fakeroot do_getty_fixup() {
	# Our machine configuration specifies which TTY's are to be used.
	# This code is similar to the Yocto systemd-serialgetty.bb
	# recipe. We ignore the baudrate currently however, and leave
	# this up to Ubuntu
	if [ ! -z "${SERIAL_CONSOLES}" ] ; then
		if [ -d ${APTGET_CHROOT_DIR}${sysconfdir}/systemd/system/getty.target.wants/ ]; then
			if [ -e ${APTGET_CHROOT_DIR}${systemd_unitdir}/system/serial-getty@.service ]; then

				tmp="${SERIAL_CONSOLES}"
				for entry in $tmp ; do
					baudrate=`echo $entry | sed 's/\;.*//'`
					ttydev=`echo $entry | sed -e 's/^[0-9]*\;//' -e 's/\;.*//'`

					# enable the service
					ln -sf ${systemd_unitdir}/system/serial-getty@.service \
						${APTGET_CHROOT_DIR}${sysconfdir}/systemd/system/getty.target.wants/serial-getty@$ttydev.service
				done
			fi
		fi
	fi
}

IMAGE_ROOTFS_SIZE ?= "8192"
IMAGE_ROOTFS_EXTRA_SPACE:append = "${@bb.utils.contains("DISTRO_FEATURES", "systemd", " + 4096", "" ,d)}"

# Add sja1110 driver for RDB boards
IMAGE_INSTALL:append:s32g274ardb2 = " sja1110"
IMAGE_INSTALL:append:s32g399ardb3 = " sja1110"

COMPATIBLE_MACHINE ="(.*ubuntu)"
