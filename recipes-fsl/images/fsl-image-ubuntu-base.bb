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

APTGET_CHROOT_DIR = "${IMAGE_ROOTFS}"
APTGET_SKIP_UPGRADE = "1"

ROOTFS_POSTPROCESS_COMMAND_append = "do_aptget_update; do_update_host; do_update_dns; do_enable_network_manager; do_systemd_service_fixup; do_getty_fixup; "

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

# Minimum support for LS2 and S32V specific elements.
IMAGE_INSTALL_append_fsl-lsch3 += "\
    mc-utils-image \
    restool \
"

# We want easy installation of the BlueBox image to the target
# Supported for any Layerscape Gen3 except LX2
DEPLOYSCRIPTS ?= "bbdeployscripts"
DEPLOYSCRIPTS_lx2160a = ""
DEPENDS_append_fsl-lsch3 = " \
    ${DEPLOYSCRIPTS} \
"

# Support for SJA1105 swich under Linux
IMAGE_INSTALL_append_s32v234bbmini += "\
    sja1105 \
"
IMAGE_INSTALL_append_s32v234evb += "\
    sja1105 \
"

require ${@bb.utils.contains('DISTRO_FEATURES', 'pfe', 'recipes-fsl/images/fsl-image-pfe.inc', '', d)}

fakeroot do_update_host() {
	set -x

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

	set +x
}

fakeroot do_update_dns() {
	set -x

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

	set +x
}

fakeroot do_enable_network_manager() {
	set -x

	# In bionic, but not in xenial. We want all [network] interfaces to be managed
	# so that we do not have to mess with interface files individually
	if [ -e "${APTGET_CHROOT_DIR}/usr/lib/NetworkManager/conf.d/10-globally-managed-devices.conf" ]; then
		sed -i -E "s/^unmanaged-devices\=\*/unmanaged-devices\=none/g" "${APTGET_CHROOT_DIR}/usr/lib/NetworkManager/conf.d/10-globally-managed-devices.conf"
	fi

	set +x
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
IMAGE_ROOTFS_EXTRA_SPACE_append = "${@bb.utils.contains("DISTRO_FEATURES", "systemd", " + 4096", "" ,d)}"

# Add LLCE CAN if needed
IMAGE_INSTALL_append_s32g2 = "${@bb.utils.contains('DISTRO_FEATURES', 'llce-can', ' linux-firmware-llce-can', '', d)}"

COMPATIBLE_MACHINE ="(.*ubuntu)"
