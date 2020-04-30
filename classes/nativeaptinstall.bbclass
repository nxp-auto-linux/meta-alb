# The purpose of this class is to simplify the process of installing debian packages
#    to a debian/ubuntu root file system.
# It transparently manages the setup of the pseudo fake root environment and
#    the installation of the packages with all their prerequisites.
# Any additional configuration of the target package management system is also 
#    handled transparently as needed.
#
# Custom shell operations that require chroot can also be executed using this class,
#    by adding them to a function named 'do_aptget_user_update' and defined in the
#    caller recipe, and executing the task do_aptget_update then.
#
# All that is required in order to use this class is:
# - define variables:
#   APTGET_CHROOT_DIR - the full path to the root filesystem where the packages
#                       will be installed
#   APTGET_EXTRA_PACKAGES - the list of debian packages (space separated) to be
#                           installed over the existing root filesystem
#   APTGET_EXTRA_PACKAGES_LAST - the list of debian packages (space separated) to be
#                           installed over the existing root filesystem, after all packages
#                           in APTGET_EXTRA_PACKAGES is installed and all operations in
#                           'do_aptget_update' have been executed
#   APTGET_EXTRA_SOURCE_PACKAGES - the list of debian source packages (space separated)
#                                  to be installed over the existing root filesystem
#   APTGET_EXTRA_PACKAGES_SERVICES_DISABLED - the list of debian packages (space separated)
#                                  to be installed over the existing root filesystem, which
#                                  must not allow any services to be (re)started. They
#                                  will be installed before packages in APTGET_EXTRA_PACKAGES,
#                                  since most probably they are (unwanted) dependencies
#   APTGET_EXTRA_LIBRARY_PATH - extra paths to search target libraries, separated by ':'
#   APTGET_EXTRA_PPA - extra PPA definitions, using format 'ADDRESS;KEY_SERVER;KEY_HASH[;type[;name]]',
#                      separated by space, where type and name are optional.
#                      'type' can be 'deb' or 'deb-src' (default 'deb')
#                      'name' if specified will be created under '/etc/apt/sources.list.d/';
#                      otherwise the PPA string will be appended to '/etc/apt/sources.list'
#   APTGET_ADD_USERS - users to be added to the file system (space separated), following
#                      format 'name:pass:shell'.
#                      'name' is the user name.
#                      'pass' is an encrypted password (e.g. generated with 
#                          `echo "P4sSw0rD" | openssl passwd -stdin`). If empty or missing, 
#                      they'll get an empty password. If you get 'pass' containing ':'
#                      then generate it again.
#                      'shell' is the default shell (if empty, default is /bin/sh).
#   APTGET_SKIP_UPGRADE - (optional) prevent running apt-get upgrade on the root filesystem
#   APTGET_SKIP_FULLUPGRADE - (optional) prevent running apt-get full-upgrade on the root filesystem
#   APTGET_YOCTO_TRANSLATION - (optional) pairs of <debianpkgname>:<commalistofyoctopkgnames>
#                      to automatically correct dependencies
#   APTGET_INIT_PACKAGES - (optional) For apt to work right on arbitrary setups, some
#                      minimum packages are needed. This is preset appropriately but may be changed.
# - define function 'do_aptget_user_update' (optional) containing all custom processing that
#          normally require to be executed under chroot (with root privileges)
# - call function 'do_aptget_update' either directly (e.g. call it from 'do_install')
#        or indirectly (e.g. add it to the variable 'ROOTFS_POSTPROCESS_COMMAND')
#
# Prerequisites:
# - The root file system must already be generated under ${APTGET_CHROOT_DIR} (e.g 
#    from a debian/ubuntu CD image or by running debootstrap)
#
# Note: If your host requires a proxy to connect to the internet, then you should use the same
# configuration for the chroot environment where the root filesystem to be updated.
# For this purpose you should set the following variables (preferably in local.conf):
# ENV_HOST_PROXIES - a space separated list of host side temporary proxies, e.g.
#     ENV_HOST_PROXIES = "http_proxy=http://my.proxy.nxp.com:8080 \
#                         https_proxy=http://my.proxy.nxp.com:8080 "
# APTGET_HOST_PROXIES - a space separated list of 'Acquire' options to be written to the apt.conf from
#                       the target root filesystem, which is used during the filesystem update, e.g.:
#     APTGET_HOST_PROXIES = "Acquire::http::proxy \"my.proxy.nxp.com:8080/\"; \
#                            Acquire::http::proxy \"my.proxy.nxp.com:8080/\"; "
# Normally only the http(s) proxy is required (to be added to ENV_HOST_PROXIES). 
# APTGET_HOST_PROXIES, if missing, is generated from the proxy data in ENV_HOST_PROXIES.

APTGET_EXTRA_PACKAGES ?= ""
APTGET_EXTRA_PACKAGES_LAST ?= ""
APTGET_EXTRA_SOURCE_PACKAGES ?= ""
APTGET_EXTRA_PACKAGES_SERVICES_DISABLED ?= ""

# Parent recipes must define the path to the root filesystem to be updated
APTGET_CHROOT_DIR ?= "${D}"

# Set this to anything but 0 to skip performing apt-get upgrade
APTGET_SKIP_UPGRADE ?= "1"

# Set this to anything but 0 to skip performing apt-get full-upgrade
APTGET_SKIP_FULLUPGRADE ?= "1"

# Set this to anything but 0 to skip performing apt-get clean at the end
APTGET_SKIP_CACHECLEAN ?= "0"

# Minimum package needs for apt to work right. Nothing else.
APTGET_INIT_PACKAGES ?= "apt-transport-https ca-certificates software-properties-common apt-utils"

APTGET_DL_CACHE ?= "${DL_DIR}/apt-get/${TRANSLATED_TARGET_ARCH}"
APTGET_CACHE_DIR ?= "${APTGET_CHROOT_DIR}/var/cache/apt/archives"

DEPENDS += "qemu-native virtual/${TARGET_PREFIX}binutils rsync-native coreutils-native"

# script and function references which reside in a different location
# in staging, or references that have to be taken from chroot afterall.
PSEUDO_CHROOT_XTRANSLATION = ""

# To run native executables required by some installation scripts
PSEUDO_CHROOT_XPREFIX="${STAGING_BINDIR_NATIVE}/qemu-${TRANSLATED_TARGET_ARCH}"

# When running in qemu, we don't really want libpseudo as qemu is already
# running with libpseudo. We want to be as chroot as possible and we
# really only want to run native things inside pseudo chroot
APTGET_EXTRA_LIBRARY_PATH_COLON="${@":".join((d.getVar("APTGET_EXTRA_LIBRARY_PATH") or "").split())}"
QEMU_SET_ENV="PATH=/usr/local/bin:/usr/local/sbin:/usr/bin:/usr/sbin:/bin:/sbin,LD_LIBRARY_PATH=${APTGET_EXTRA_LIBRARY_PATH_COLON},PSEUDO_PASSWD=${APTGET_CHROOT_DIR},LC_ALL=C,DEBIAN_FRONTEND=noninteractive"
QEMU_UNSET_ENV="LD_PRELOAD,APT_CONFIG"

# This is an ugly one, but I haven't come up yet with a neat solution.
# It turns out that PAM rejects audit_log_acct_message() because the
# PAM service runs on the host and our pseudo chroot setup does not
# run as real root. So it doesn't matter that our /etc/passwd file
# really is inside the fakeroot because the authentication check is
# done outside. This affects any host with PAM enabled. We also
# can't just grab the library call in pseudo because it actually runs
# inside the qemu environment fully emulated ... where pseudo is not
# applied.
# As quick hack/fix, we just don't do chfn ...
PSEUDO_CHROOT_XTRANSLATION="chfn=/bin/true"

# We force default PATH related elements into chroot as well as
# any full path executables and scripts
PSEUDO_CHROOT_FORCED="\
/usr/local/bin:\
/usr/local/sbin:\
/usr/bin:\
/usr/sbin:\
/bin:\
/sbin:\
/root:\
/*:\
"

# Some things we always want from the host. This is pseudo related
# stuff and also dynamic fs elements.
PSEUDO_CHROOT_EXCEPTIONS="\
${PSEUDO_CHROOT_XPREFIX}:\
${PSEUDO_PREFIX}/*:\
${PSEUDO_LIBDIR}*/*:\
${PSEUDO_LOCALSTATEDIR}*:\
${PSEUDO_LOCALSTATEDIR}:\
/dev/null:\
/dev/zero:\
/dev/random:\
/dev/urandom:\
/dev/tty:\
/dev/pts:\
/dev/pts/*:\
/dev/ptmx:\
"

ENV_HOST_PROXIES ?= ""
APTGET_HOST_PROXIES ?= ""
APTGET_EXECUTABLE ?= "/usr/bin/apt-get"
APTGET_DEFAULT_OPTS ?= "-qy"

aptget_update_presetvars() {
	export PSEUDO_PASSWD="${APTGET_CHROOT_DIR}:${STAGING_DIR_NATIVE}"

	# All this depends on the updated pseudo-native with better 
	# chroot support. Without it, apt-get will fail.
	export PSEUDO_CHROOT_XTRANSLATION="${PSEUDO_CHROOT_XTRANSLATION}"
	export PSEUDO_CHROOT_FORCED="${PSEUDO_CHROOT_FORCED}"
	export PSEUDO_CHROOT_EXCEPTIONS="${PSEUDO_CHROOT_EXCEPTIONS}"

	# With this little trick, we can qemu target-side executables
	# inside pseudo chroot without losing pseudo functionality.
	# This is a must have for some of the package related scripts
	# that have to use the target side executables.
	# This depends on both our pseudo and qemu update
	export PSEUDO_CHROOT_XPREFIX="${PSEUDO_CHROOT_XPREFIX}"
	export QEMU_SET_ENV="${QEMU_SET_ENV}"
	export QEMU_UNSET_ENV="${QEMU_UNSET_ENV}"
	export QEMU_LIBCSYSCALL="1"
	#unset QEMU_LD_PREFIX

	# Add any proxies from the host, according to
	# https://wiki.yoctoproject.org/wiki/Working_Behind_a_Network_Proxy
	ENV_HOST_PROXIES="${ENV_HOST_PROXIES}"

	while [ -n "$ENV_HOST_PROXIES" ]; do
		IFS=" =_" read -r proxy_type proxy_string proxy_val ENV_HOST_PROXIES <<END_PROXY
$ENV_HOST_PROXIES
END_PROXY
		if [ "$proxy_string" != "proxy" ]; then
			bbwarn "Invalid proxy \"$proxy\""
			continue
		fi

		export QEMU_SET_ENV="$QEMU_SET_ENV,${proxy_type}_${proxy_string}=$proxy_val"

		# If APTGET_HOST_PROXIES is not defined in local.conf, then
		# apt.conf is populated using proxy information in ENV_HOST_PROXIES
		if [ -z "${APTGET_HOST_PROXIES}" ]; then
			echo >>"${APTGET_CHROOT_DIR}/etc/apt/apt.conf" "Acquire::$proxy_type::proxy \"$proxy_val/\"; /* Yocto */"
		fi
	done

	export etc_hosts_renamed="${APTGET_CHROOT_DIR}/etc/hosts.yocto"
	export etc_resolv_conf_renamed="${APTGET_CHROOT_DIR}/etc/resolv.conf.yocto"
}

fakeroot aptget_preserve_file() {
        if [ -e "${APTGET_CHROOT_DIR}$1" ]; then
                rm -f "${APTGET_CHROOT_DIR}$1.yocto"
                mv "${APTGET_CHROOT_DIR}$1" "${APTGET_CHROOT_DIR}$1.yocto"
        fi
}

fakeroot aptget_restore_file() {
        if [ -e "${APTGET_CHROOT_DIR}$1.yocto" ]; then
                rm -f "${APTGET_CHROOT_DIR}$1"
                mv "${APTGET_CHROOT_DIR}$1.yocto" "${APTGET_CHROOT_DIR}$1"
        fi
}

fakeroot aptget_populate_cache_from_sstate() {
	if [ -e "${APTGET_CACHE_DIR}" ]; then
		mkdir -p "${APTGET_DL_CACHE}"
		chroot "${APTGET_CHROOT_DIR}" ${APTGET_EXECUTABLE} ${APTGET_DEFAULT_OPTS} check
		rsync -v -d -u -t --include *.deb "${APTGET_DL_CACHE}/" "${APTGET_CACHE_DIR}"
		chroot "${APTGET_CHROOT_DIR}" ${APTGET_EXECUTABLE} ${APTGET_DEFAULT_OPTS} check
	fi
}

fakeroot aptget_save_cache_into_sstate() {
	if [ -e "${APTGET_CACHE_DIR}" ]; then
		mkdir -p "${APTGET_DL_CACHE}"
		chroot "${APTGET_CHROOT_DIR}" ${APTGET_EXECUTABLE} ${APTGET_DEFAULT_OPTS} check
		rsync -v -d -u -t --include *.deb "${APTGET_CACHE_DIR}/" "${APTGET_DL_CACHE}" 
	fi
}

fakeroot aptget_update_begin() {
	# Once the basic rootfs is unpacked, we use the local passwd
	# information.
	set -x

	aptget_update_presetvars;

	aptgetfailure=0
	# While we do our installation stunt in qemu land, we also want
	# to be able to use host side networking configs. This means we
	# need to protect the host and DNS config. We do a bit of a
	# convoluted stunt here to hopefully be flexible enough about
	# different rootfs types.
	if [ -e "${APTGET_CHROOT_DIR}/etc/hosts" ]; then
		rm -f "$etc_hosts_renamed"
		mv "${APTGET_CHROOT_DIR}/etc/hosts" "$etc_hosts_renamed"
	fi
	cp "/etc/hosts" "${APTGET_CHROOT_DIR}/etc/hosts"
	if [ -e "${APTGET_CHROOT_DIR}/etc/resolv.conf" ]; then
		rm -f "$etc_resolv_conf_renamed"
		mv "${APTGET_CHROOT_DIR}/etc/resolv.conf" "$etc_resolv_conf_renamed"
	fi
	cp "/etc/resolv.conf" "${APTGET_CHROOT_DIR}/etc/resolv.conf"

	# apt may not be fully configured at this stage
	mkdir -p "${APTGET_CHROOT_DIR}/etc/apt"

	APTGET_HOST_PROXIES="${APTGET_HOST_PROXIES}"
	while [ -n "$APTGET_HOST_PROXIES" ]; do
		read -r proxy <<END_PROXY
$APTGET_HOST_PROXIES
END_PROXY
		echo >>"${APTGET_CHROOT_DIR}/etc/apt/apt.conf" "$proxy"
	done

	# We need to set at least one (dummy) user and we set passwords for all of them.
	# useradd is not debian, but good enough for now.
	# Technically, this should be done at image generation time,
	# but the default Yocto mechanisms are a bit intrusive.
	# This needs some research. UNDERSTAND AND FIX!
	# In any case, this needs to run as chroot so that we modify
	# the proper passwd/group inside pseudo.
	# The Ubuntu 'adduser' doesn't work because passwd is called
	# which doesn't like our pseudo root
	if [ -n "${APTGET_ADD_USERS}" ]; then
		# Tricky variable hack to get word parsing for Yocto
		# variables in the shell.
		x="${APTGET_ADD_USERS}"
		for user in $x; do

			IFS=':' read -r user_name user_passwd user_shell <<END_USER
$user
END_USER

			if [ -z "$user_name" ]; then
				bbwarn "Empty user name, skipping."
				continue
			fi
			if [ -z "$user_passwd" ]; then
				# encrypted empty password
				user_passwd="BB.jlCwQFvebE"
			fi

			user_shell_opt=""
			if [ -n "$user_shell" ]; then
				user_shell_opt="-s $user_shell"
			fi

			if [ -z "`cat ${APTGET_CHROOT_DIR}/etc/passwd | grep $user_name`" ]; then
				chroot "${APTGET_CHROOT_DIR}" /usr/sbin/useradd -p "$user_passwd" -U -G sudo,users -m "$user_name" $user_shell_opt
			fi

		done
	fi

	# Yocto environment. If we kept apt packages privately from
	# a prior run, prepopulate the package cache locally to avoid
	# costly downloads
	aptget_populate_cache_from_sstate

	# Before we can play with the package manager in any
	# meaningful way, we need to sync the database.
	if [ -n "${APTGET_EXTRA_SOURCE_PACKAGES}" ]; then
		if grep '# deb-src' ${APTGET_CHROOT_DIR}/etc/apt/sources.list; then
			chroot "${APTGET_CHROOT_DIR}" /bin/sed -i 's/# deb-src/deb-src/g' /etc/apt/sources.list
		fi
	fi

        # This is magic to fool package installations into thinking
        # better about the rootfs
        mkdir -p "${APTGET_CHROOT_DIR}/proc/1"
        ln -s "/" "${APTGET_CHROOT_DIR}/proc/1/root"

	# Prepare apt to be generically usable
	chroot "${APTGET_CHROOT_DIR}" ${APTGET_EXECUTABLE} ${APTGET_DEFAULT_OPTS} update
	if [ -n "${APTGET_INIT_PACKAGES}" ]; then
		x="${APTGET_INIT_PACKAGES}"
		test $aptgetfailure -ne 0 || chroot "${APTGET_CHROOT_DIR}" ${APTGET_EXECUTABLE} ${APTGET_DEFAULT_OPTS} install $x || aptgetfailure=1
	fi

	if [ -n "${APTGET_EXTRA_PPA}" ]; then
		DISTRO_NAME=`grep "DISTRIB_CODENAME=" "${APTGET_CHROOT_DIR}/etc/lsb-release" | sed "s/DISTRIB_CODENAME=//g"`
		DISTRO_RELEASE=`grep "DISTRIB_RELEASE=" "${APTGET_CHROOT_DIR}/etc/lsb-release" | sed "s/DISTRIB_RELEASE=//g"`

		if [ -z "$DISTRO_NAME" ]; then 
			bberror "Unable to get target linux distribution codename. Please check that \"${APTGET_CHROOT_DIR}/etc/lsb-release\" is not corrupted."
		fi

		# For apt-key to be reliable, we need both gpg and dirmngr
		# As workaround for an 18.04 gpg regressions, we also use curl
		APTGET_GPG_BROKEN=""
		if [ "$DISTRO_RELEASE" = "18.04" ]; then
			APTGET_GPG_BROKEN="1"
		fi
		if [ -n "$APTGET_GPG_BROKEN" ]; then
			test $aptgetfailure -ne 0 || chroot "${APTGET_CHROOT_DIR}" ${APTGET_EXECUTABLE} ${APTGET_DEFAULT_OPTS} install curl gnupg2 || aptgetfailure=1
		else
			test $aptgetfailure -ne 0 || chroot "${APTGET_CHROOT_DIR}" ${APTGET_EXECUTABLE} ${APTGET_DEFAULT_OPTS} install gnupg2 dirmngr || aptgetfailure=1
		fi

		# Tricky variable hack to get word parsing for Yocto
		# variables in the shell.
		x="${APTGET_EXTRA_PPA}"
		for ppa in $x; do

			IFS=';' read -r ppa_addr ppa_server ppa_hash ppa_type ppa_file_orig <<END_PPA
$ppa
END_PPA

			if [ "`echo $ppa_addr | head -c 4`" = "ppa:" ]; then
				chroot "${APTGET_CHROOT_DIR}" /usr/bin/add-apt-repository -y -s $ppa_addr
				continue;
			fi

			if [ -z "$ppa_type" ]; then
				ppa_type="deb"
			fi
			if [ -n "$ppa_file_orig" ]; then
				ppa_file="/etc/apt/sources.list.d/$ppa_file_orig"
			else
				ppa_file="/etc/apt/sources.list"
			fi
			ppa_proxy=""
			if [ -n "$ENV_HTTP_PROXY" ]; then
				if [ -n "$APTGET_GPG_BROKEN" ]; then
					ppa_proxy="-proxy=$ENV_HTTP_PROXY"
				else
					ppa_proxy="--keyserver-options http-proxy=$ENV_HTTP_PROXY"
				fi
			fi

			echo >>"${APTGET_CHROOT_DIR}/$ppa_file" "$ppa_type $ppa_addr $DISTRO_NAME main"
			if [ -n "$APTGET_GPG_BROKEN" ]; then
				HTTPPPASERVER=`echo $ppa_server | sed "s/hkp:/http:/g"`
				mkdir -p "${APTGET_CHROOT_DIR}/tmp/gpg"
				chmod 0600 "${APTGET_CHROOT_DIR}/tmp/gpg"
				chroot "${APTGET_CHROOT_DIR}" /usr/bin/curl -sL "$HTTPPPASERVER/pks/lookup?op=get&search=0x$ppa_hash" | chroot "${APTGET_CHROOT_DIR}" /usr/bin/gpg --homedir /tmp/gpg --import || true
				chroot "${APTGET_CHROOT_DIR}" /usr/bin/gpg --homedir /tmp/gpg --export $ppa_hash | chroot "${APTGET_CHROOT_DIR}" /usr/bin/tee "/etc/apt/trusted.gpg.d/$ppa_file_orig.gpg"
				rm -rf "${APTGET_CHROOT_DIR}/tmp/gpg"
			else
				chroot "${APTGET_CHROOT_DIR}" /usr/bin/apt-key adv --keyserver $ppa_server $ppa_proxy --recv-key $ppa_hash
			fi

		done
		test $aptgetfailure -ne 0 || chroot "${APTGET_CHROOT_DIR}" ${APTGET_EXECUTABLE} ${APTGET_DEFAULT_OPTS} update || aptgetfailure=1
	fi

	if [ "${APTGET_SKIP_UPGRADE}" = "0" ]; then
		test $aptgetfailure -ne 0 || chroot "${APTGET_CHROOT_DIR}" ${APTGET_EXECUTABLE} ${APTGET_DEFAULT_OPTS} -f install || aptgetfailure=1
		test $aptgetfailure -ne 0 || chroot "${APTGET_CHROOT_DIR}" ${APTGET_EXECUTABLE} ${APTGET_DEFAULT_OPTS} upgrade || aptgetfailure=1
	fi

	if [ "${APTGET_SKIP_FULLUPGRADE}" = "0" ]; then
		test $aptgetfailure -ne 0 || chroot "${APTGET_CHROOT_DIR}" ${APTGET_EXECUTABLE} ${APTGET_DEFAULT_OPTS} -f install || aptgetfailure=1
		test $aptgetfailure -ne 0 || chroot "${APTGET_CHROOT_DIR}" ${APTGET_EXECUTABLE} ${APTGET_DEFAULT_OPTS} full-upgrade || aptgetfailure=1
	fi

	if [ -n "${APTGET_EXTRA_PACKAGES_SERVICES_DISABLED}" ]; then
		# workaround - deny (re)starting of services, for selected packages, since
		# they will make the installation fail
                aptget_preserve_file "/usr/sbin/policy-rc.d"
		echo  >"${APTGET_CHROOT_DIR}/usr/sbin/policy-rc.d" "#!/bin/sh"
		echo >>"${APTGET_CHROOT_DIR}/usr/sbin/policy-rc.d" "exit 101"
		chmod a+x "${APTGET_CHROOT_DIR}/usr/sbin/policy-rc.d"

		test $aptgetfailure -ne 0 || chroot "${APTGET_CHROOT_DIR}" ${APTGET_EXECUTABLE} ${APTGET_DEFAULT_OPTS} install ${APTGET_EXTRA_PACKAGES_SERVICES_DISABLED} || aptgetfailure=1

		# remove the workaround
		rm -rf "${APTGET_CHROOT_DIR}/usr/sbin/policy-rc.d"
                aptget_restore_file "/usr/sbin/policy-rc.d"
	fi

	if [ -n "${APTGET_EXTRA_PACKAGES}" ]; then
                # Very ugly workaround. Turns out that some packages use
                # lsmod, e.g., console-setup. lsmod wants to access the
                # module database via sysfs. We are not in a live system,
                # so sysfs does not exist, which leads to errors. These
                # errors then make an otherwise perfectly valid install
                # fail. Our workaround is to temporarily replace lsmod.
                # This is ok as we don't have any modules loaded anyway.
                x="/bin/lsmod"
                if [ -e $x ]; then
                        aptget_preserve_file $x
                        echo  >"${APTGET_CHROOT_DIR}$x" "#!/bin/sh"
                        echo  >>"${APTGET_CHROOT_DIR}$x" "echo 'Module                  Size  Used by'"
                        chmod a+x "${APTGET_CHROOT_DIR}$x"
                fi

                test $aptgetfailure -ne 0 || chroot "${APTGET_CHROOT_DIR}" ${APTGET_EXECUTABLE} ${APTGET_DEFAULT_OPTS} install ${APTGET_EXTRA_PACKAGES} || aptgetfailure=1

                if [ -e $x ]; then
                        aptget_restore_file $x
                fi
	fi

        # Obviously we can't have a /proc/1 in an offline rootfs.
        # So we remove our temporary helper again
        rm -fr "${APTGET_CHROOT_DIR}/proc/1"

	if [ -n "${APTGET_EXTRA_SOURCE_PACKAGES}" ]; then
		# We need this to get source package handling properly
		# configured for a subsequent apt-get source
		test $aptgetfailure -ne 0 || chroot "${APTGET_CHROOT_DIR}" ${APTGET_EXECUTABLE} ${APTGET_DEFAULT_OPTS} install dpkg-dev || aptgetfailure=1

		# For lack of a better idea, we install source packages
		# into the root user's home. if we could guarantee that
		# they are all read only, /opt might be a good place.
		# But we can't guarantee that.
		# Net result is that we use an ugly hack to overcome
		# the chroot directory problem.
		echo  >"${APTGET_CHROOT_DIR}/aptgetsource.sh" "#!/bin/sh"
		echo >>"${APTGET_CHROOT_DIR}/aptgetsource.sh" "cd \$1"
		echo >>"${APTGET_CHROOT_DIR}/aptgetsource.sh" "${APTGET_EXECUTABLE} ${APTGET_DEFAULT_OPTS} source \$2"
		x="${APTGET_EXTRA_SOURCE_PACKAGES}"
		for i in $x; do
			test $aptgetfailure -ne 0 || chroot "${APTGET_CHROOT_DIR}" /bin/bash /aptgetsource.sh "/root" "${i}" || aptgetfailure=1
		done
		rm -f "${APTGET_CHROOT_DIR}/aptgetsource.sh"
	fi

	# Once we have done the installation, save off the package
	# cache locally for repeated use of recipe building
	# We also try to save the cache in case of package install errors
	# to avoid downloads on a subsequent attempt
	aptget_save_cache_into_sstate

	if [ $aptgetfailure -ne 0 ]; then
		bberror "${APTGET_EXECUTABLE} failed to execute as expected!"
		return $aptgetfailure
	fi

	# The list of installed packages goes into the log
	echo "Installed packages:"
	chroot "${APTGET_CHROOT_DIR}" /usr/bin/dpkg -l | grep '^ii' | awk '{print $2}'

	set +x
}

# Must have to preset all variables properly. It also means that
# the user of this class should not prepend to avoid ordering issues.
fakeroot do_aptget_user_update_prepend() {

	aptget_update_presetvars;
}

# empty placeholder, override it in parent script for more functionality
fakeroot do_aptget_user_update() {

	:
}

fakeroot aptget_update_end() {

	set -x

	aptget_update_presetvars;

	aptgetfailure=0
	if [ -n "${APTGET_EXTRA_PACKAGES_LAST}" ]; then
		test $aptgetfailure -ne 0 || chroot "${APTGET_CHROOT_DIR}" ${APTGET_EXECUTABLE} ${APTGET_DEFAULT_OPTS} install ${APTGET_EXTRA_PACKAGES_LAST} || aptgetfailure=1
	fi

	# Once we have done the installation, save off the package
	# cache locally for repeated use of recipe building
	aptget_save_cache_into_sstate

	if [ "${APTGET_SKIP_CACHECLEAN}" = "0" ]; then
		test $aptgetfailure -ne 0 || chroot "${APTGET_CHROOT_DIR}" ${APTGET_EXECUTABLE} ${APTGET_DEFAULT_OPTS} clean
	fi

	# Delete any temp proxy lines we may have added in the target rootfs
	if [ -f "${APTGET_CHROOT_DIR}/etc/apt/apt.conf" ]; then
		sed -i '/^Acquire::.+; \/* Yocto *\/\s*$/d' "${APTGET_CHROOT_DIR}/etc/apt/apt.conf"
	fi

	# Now that we are done in qemu land, we reinstate the original
	# networking config of our target rootfs.
	# We really should do this only if the targets have not been
	# modified during installation. Hmm. Remove vs. Merge? FIX?
	if [ -e "$etc_hosts_renamed" ]; then
		mv -f "$etc_hosts_renamed" "${APTGET_CHROOT_DIR}/etc/hosts" 
	fi
	if [ -e "$etc_resolv_conf_renamed" ]; then
		if [ ! -L "${APTGET_CHROOT_DIR}/etc/resolv.conf" ]; then
			mv -f "$etc_resolv_conf_renamed" "${APTGET_CHROOT_DIR}/etc/resolv.conf"
		else
			rm -f "$etc_resolv_conf_renamed"
		fi
	fi

	if [ $aptgetfailure -ne 0 ]; then
		bberror "${APTGET_EXECUTABLE} failed to execute as expected!"
		return $aptgetfailure
	fi

	set +x
}

python do_aptget_update() {
    bb.build.exec_func("aptget_update_begin", d);
    bb.build.exec_func("do_aptget_user_update", d);
    bb.build.exec_func("aptget_update_end", d);
}

# The various apt packages need to be translated properly into Yocto
# RPROVIDES_${PN}
APTGET_ALL_PACKAGES = "\
        ${APTGET_INIT_PACKAGES} \
        ${APTGET_EXTRA_PACKAGES} \
	${APTGET_EXTRA_PACKAGES_LAST} \
	${APTGET_EXTRA_SOURCE_PACKAGES} \
	${APTGET_EXTRA_PACKAGES_SERVICES_DISABLED} \
	${APTGET_RPROVIDES} \
"

# We have some preconceived notions in APTGET_YOCTO_TRANSLATION about
# the Yocto names we find in the current layer set. If the translation
# does not match the actual packages, you can get rather weird dependency
# resolutions that mess up the final result. Rather than silently
# messing things up, we will hardcode our compatibility here and
# complain if needed!
python() {
        lc = d.getVar("LAYERSERIES_CORENAMES")
        if "zeus" not in lc:
                bb.error("nativeaptinstall.bbclass is incompatible to the current layer set")
                bb.error("You must check APTGET_YOCTO_TRANSLATION and update the anonymous python() function!")
}
# Now we translate the various debian package names into Yocto names
# to be able to set up RPROVIDERS and the like properly. The list
# below is not exhaustive but covers the currently known use cases.
# If suddenly Yocto packages show up in the image when Ubuntu already
# provides the solution, the likelihood is high that a name translation
# may be missing.
APTGET_YOCTO_TRANSLATION += "\
    libdb5.3:db \
    device-tree-compiler:dtc \
    libffi6:libffi7 \
    libnss-db:libnss-db2 \
    libpam0g:libpam \
    libssl1.0.0:libssl1.0 \
    libssl1.1:libssl1.1 \
    libssl-dev:openssl-dev,openssl-qoriq-dev \
    openssl:openssl-bin,openssl-qoriq-bin,openssl-conf,openssl-qoriq-conf,openssl-misc,openssl-qoriq-misc \
    python3.5:python3 \
    xz-utils:xz \
    zlib1g:libz1 \
"
# This is a really ugly one for us because Yocto does a very fine
# grained split of libc. Note how we avoid spaces in the wrong places!
APTGET_YOCTO_TRANSLATION += "\
	libc6:libc6,glibc,eglibc\
glibc-thread-db,eglibc-thread-db,\
glibc-extra-nss,eglibc-extra-nss,\
glibc-pcprofile,eglibc-pcprofile,\
libsotruss,libcidn,libmemusage,libsegfault,\
glibc-gconv-ansi-x3.110,glibc-gconv-armscii-8,glibc-gconv-asmo-449,\
glibc-gconv-big5hkscs,glibc-gconv-big5,glibc-gconv-brf,\
glibc-gconv-cp10007,glibc-gconv-cp1125,glibc-gconv-cp1250,\
glibc-gconv-cp1251,glibc-gconv-cp1252,glibc-gconv-cp1253,\
glibc-gconv-cp1254,glibc-gconv-cp1255,glibc-gconv-cp1256,\
glibc-gconv-cp1257,glibc-gconv-cp1258,glibc-gconv-cp737,\
glibc-gconv-cp770,glibc-gconv-cp771,glibc-gconv-cp772,\
glibc-gconv-cp773,glibc-gconv-cp774,glibc-gconv-cp775,\
glibc-gconv-cp932,glibc-gconv-csn-369103,glibc-gconv-cwi,\
glibc-gconv-dec-mcs,glibc-gconv-ebcdic-at-de-a,\
glibc-gconv-ebcdic-at-de,glibc-gconv-ebcdic-ca-fr,\
glibc-gconv-ebcdic-dk-no-a,glibc-gconv-ebcdic-dk-no,\
glibc-gconv-ebcdic-es-a,glibc-gconv-ebcdic-es,\
glibc-gconv-ebcdic-es-s,glibc-gconv-ebcdic-fi-se-a,\
glibc-gconv-ebcdic-fi-se,glibc-gconv-ebcdic-fr,\
glibc-gconv-ebcdic-is-friss,glibc-gconv-ebcdic-it,\
glibc-gconv-ebcdic-pt,glibc-gconv-ebcdic-uk,glibc-gconv-ebcdic-us,\
glibc-gconv-ecma-cyrillic,glibc-gconv-euc-cn,\
glibc-gconv-euc-jisx0213,glibc-gconv-euc-jp-ms,glibc-gconv-euc-jp,\
glibc-gconv-euc-kr,glibc-gconv-euc-tw,glibc-gconv-gb18030,\
glibc-gconv-gbbig5,glibc-gconv-gbgbk,glibc-gconv-gbk,\
glibc-gconv-georgian-academy,glibc-gconv-georgian-ps,\
glibc-gconv-gost-19768-74,glibc-gconv-greek7-old,glibc-gconv-greek7,\
glibc-gconv-greek-ccitt,glibc-gconv-hp-greek8,glibc-gconv-hp-roman8,\
glibc-gconv-hp-roman9,glibc-gconv-hp-thai8,glibc-gconv-hp-turkish8,\
glibc-gconv-ibm037,glibc-gconv-ibm038,glibc-gconv-ibm1004,\
glibc-gconv-ibm1008-420,glibc-gconv-ibm1008,glibc-gconv-ibm1025,\
glibc-gconv-ibm1026,glibc-gconv-ibm1046,glibc-gconv-ibm1047,\
glibc-gconv-ibm1097,glibc-gconv-ibm1112,glibc-gconv-ibm1122,\
glibc-gconv-ibm1123,glibc-gconv-ibm1124,glibc-gconv-ibm1129,\
glibc-gconv-ibm1130,glibc-gconv-ibm1132,glibc-gconv-ibm1133,\
glibc-gconv-ibm1137,glibc-gconv-ibm1140,glibc-gconv-ibm1141,\
glibc-gconv-ibm1142,glibc-gconv-ibm1143,glibc-gconv-ibm1144,\
glibc-gconv-ibm1145,glibc-gconv-ibm1146,glibc-gconv-ibm1147,\
glibc-gconv-ibm1148,glibc-gconv-ibm1149,glibc-gconv-ibm1153,\
glibc-gconv-ibm1154,glibc-gconv-ibm1155,glibc-gconv-ibm1156,\
glibc-gconv-ibm1157,glibc-gconv-ibm1158,glibc-gconv-ibm1160,\
glibc-gconv-ibm1161,glibc-gconv-ibm1162,glibc-gconv-ibm1163,\
glibc-gconv-ibm1164,glibc-gconv-ibm1166,glibc-gconv-ibm1167,\
glibc-gconv-ibm12712,glibc-gconv-ibm1364,glibc-gconv-ibm1371,\
glibc-gconv-ibm1388,glibc-gconv-ibm1390,glibc-gconv-ibm1399,\
glibc-gconv-ibm16804,glibc-gconv-ibm256,glibc-gconv-ibm273,\
glibc-gconv-ibm274,glibc-gconv-ibm275,glibc-gconv-ibm277,\
glibc-gconv-ibm278,glibc-gconv-ibm280,glibc-gconv-ibm281,\
glibc-gconv-ibm284,glibc-gconv-ibm285,glibc-gconv-ibm290,\
glibc-gconv-ibm297,glibc-gconv-ibm420,glibc-gconv-ibm423,\
glibc-gconv-ibm424,glibc-gconv-ibm437,glibc-gconv-ibm4517,\
glibc-gconv-ibm4899,glibc-gconv-ibm4909,glibc-gconv-ibm4971,\
glibc-gconv-ibm500,glibc-gconv-ibm5347,glibc-gconv-ibm803,\
glibc-gconv-ibm850,glibc-gconv-ibm851,glibc-gconv-ibm852,\
glibc-gconv-ibm855,glibc-gconv-ibm856,glibc-gconv-ibm857,\
glibc-gconv-ibm860,glibc-gconv-ibm861,glibc-gconv-ibm862,\
glibc-gconv-ibm863,glibc-gconv-ibm864,glibc-gconv-ibm865,\
glibc-gconv-ibm866nav,glibc-gconv-ibm866,glibc-gconv-ibm868,\
glibc-gconv-ibm869,glibc-gconv-ibm870,glibc-gconv-ibm871,\
glibc-gconv-ibm874,glibc-gconv-ibm875,glibc-gconv-ibm880,\
glibc-gconv-ibm891,glibc-gconv-ibm901,glibc-gconv-ibm902,\
glibc-gconv-ibm9030,glibc-gconv-ibm903,glibc-gconv-ibm904,\
glibc-gconv-ibm905,glibc-gconv-ibm9066,glibc-gconv-ibm918,\
glibc-gconv-ibm921,glibc-gconv-ibm922,glibc-gconv-ibm930,\
glibc-gconv-ibm932,glibc-gconv-ibm933,glibc-gconv-ibm935,\
glibc-gconv-ibm937,glibc-gconv-ibm939,glibc-gconv-ibm943,\
glibc-gconv-ibm9448,glibc-gconv-iec-p27-1,glibc-gconv-inis-8,\
glibc-gconv-inis-cyrillic,glibc-gconv-inis,glibc-gconv-isiri-3342,\
glibc-gconv-iso-10367-box,glibc-gconv-iso-11548-1,\
glibc-gconv-iso-2022-cn-ext,glibc-gconv-iso-2022-cn,\
glibc-gconv-iso-2022-jp-3,glibc-gconv-iso-2022-jp,\
glibc-gconv-iso-2022-kr,glibc-gconv-iso-2033,\
glibc-gconv-iso-5427-ext,glibc-gconv-iso-5427,glibc-gconv-iso-5428,\
glibc-gconv-iso646,glibc-gconv-iso-6937-2,glibc-gconv-iso-6937,\
glibc-gconv-iso8859-10,glibc-gconv-iso8859-11,glibc-gconv-iso8859-13,\
glibc-gconv-iso8859-14,glibc-gconv-iso8859-15,glibc-gconv-iso8859-16,\
glibc-gconv-iso8859-1,glibc-gconv-iso8859-2,glibc-gconv-iso8859-3,\
glibc-gconv-iso8859-4,glibc-gconv-iso8859-5,glibc-gconv-iso8859-6,\
glibc-gconv-iso8859-7,glibc-gconv-iso8859-8,glibc-gconv-iso8859-9e,\
glibc-gconv-iso8859-9,glibc-gconv-iso-ir-197,glibc-gconv-iso-ir-209,\
glibc-gconv-johab,glibc-gconv-koi-8,glibc-gconv-koi8-r,\
glibc-gconv-koi8-ru,glibc-gconv-koi8-t,glibc-gconv-koi8-u,\
glibc-gconv-latin-greek-1,glibc-gconv-latin-greek,glibc-gconv-libcns,\
glibc-gconv-libgb,glibc-gconv-libisoir165,glibc-gconv-libjis,\
glibc-gconv-libjisx0213,glibc-gconv-libksc,\
glibc-gconv-mac-centraleurope,glibc-gconv-macintosh,\
glibc-gconv-mac-is,glibc-gconv-mac-sami,glibc-gconv-mac-uk,\
glibc-gconv-mik,glibc-gconv-nats-dano,glibc-gconv-nats-sefi,\
glibc-gconv,glibc-gconv-pt154,glibc-gconv-rk1048,\
glibc-gconv-sami-ws2,glibc-gconv-shift-jisx0213,glibc-gconv-sjis,\
glibc-gconvs,glibc-gconv-t.61,glibc-gconv-tcvn5712-1,\
glibc-gconv-tis-620,glibc-gconv-tscii,glibc-gconv-uhc,\
glibc-gconv-unicode,glibc-gconv-utf-16,glibc-gconv-utf-32,\
glibc-gconv-utf-7,glibc-gconv-viscii\
 \
"

# If we are using this class, we want to ensure that our recipe or
# image is also properly listed as providing the needed results
python () {
    pn = (d.getVar('PN', True) or "")
    packagelist = (d.getVar('APTGET_ALL_PACKAGES', True) or "").split()
    translations = (d.getVar('APTGET_YOCTO_TRANSLATION', True) or "").split()

    origrprovides = (d.getVar('RPROVIDES_%s' % pn, True) or "").split()
    allrprovides = []
    for p in packagelist:
        appendp = True
        rprovides = [p]
        for t in translations:
            pkg,yocto = t.split(":")
            if p == pkg and yocto:
                rprovides = yocto.split(",")
                break

        for i in rprovides:
            if i and i not in allrprovides:
                allrprovides.append(i)

    if allrprovides:
        s = ' '.join(origrprovides + allrprovides)
        bb.debug(1, 'Setting RPROVIDES_%s = "%s"' % (pn, s))
        d.setVar('RPROVIDES_%s' % pn, s)
}
