# The purpose of this class is to simplify the process of installing debian packages
#    to a debian/ubuntu root file system.
# It transparently manages the setup of the pseudo fake root environment and
#    the installation of the packages with all their prerequisites.
# Any additional configuration of the target package management system is also 
#    handled transparently as needed.
#
# Custom shell operations that require chroot can also be executed using this class,
#    by adding them to a function named 'do_shell_update' and defined in the caller
#    recipe.
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
#                           'do_shell_update' have been executed
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
# - define function 'do_shell_update' (optional) containing all custom processing that
#          normally require to be executed under chroot (with root privileges)
# - call function 'do_shell_update' either directly (e.g. call it from 'do_install')
#        or indirectly (e.g. add it to the variable 'ROOTFS_POSTPROCESS_COMMAND')
#
# Prerequisites:
# - The root file system must already be generated under ${APTGET_CHROOT_DIR} (e.g 
#    from a debian/ubuntu CD image or by running debootstrap)
#
# Note: If your host requires a proxy to connect to the internet, then you should use the same
# configuration for the chroot environment where the root filesystem to be updated.
# For this purpose you should set the following variables (preferably in local.conf):
# ENV_HOST_PROXIES - a space separated list of proxies, e.g.
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
APTGET_SKIP_UPGRADE ?= "0"

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
/proc/*:\
/dev/null:\
/dev/zero:\
/dev/random:\
/dev/urandom:\
/dev/tty:\
/dev/pts:\
/dev/pts/*:\
/dev/ptmx:\
/etc/hosts:\
/etc/resolv.conf:\
"

ENV_HOST_PROXIES ?= ""
APTGET_HOST_PROXIES ?= ""

fakeroot do_shell_update_prepend() {
	# Once the basic rootfs is unpacked, we use the local passwd
	# information.
	set -x

	export PSEUDO_PASSWD="${APTGET_CHROOT_DIR}:${STAGING_DIR_NATIVE}"

	# All this depends on the updated pseudo-native with better 
	# chroot support. Without it, apt-get will fail.
	export PSEUDO_CHROOT_XTRANSLATION="${PSEUDO_CHROOT_XTRANSLATION}"
	export PSEUDO_CHROOT_FORCED="${PSEUDO_CHROOT_FORCED}"
	export PSEUDO_CHROOT_EXCEPTIONS="${PSEUDO_CHROOT_EXCEPTIONS}"

	# apt may not be fully configured at this stage
	mkdir -p "${APTGET_CHROOT_DIR}/etc/apt"

	# Add any proxies from the host, according to
	# https://wiki.yoctoproject.org/wiki/Working_Behind_a_Network_Proxy
	ENV_HOST_PROXIES="${ENV_HOST_PROXIES}"
	export QEMU_SET_ENV="${QEMU_SET_ENV}"

	while [ -n "$ENV_HOST_PROXIES" ]; do
		IFS=" =_" read -r proxy_type proxy_string proxy_val ENV_HOST_PROXIES <<END_PROXY
$ENV_HOST_PROXIES
END_PROXY
		if [ "$proxy_string" != "proxy" ]; then
			bb_warn "Invalid proxy \"$proxy\""
			continue
		fi

		export QEMU_SET_ENV="$QEMU_SET_ENV,${proxy_type}_${proxy_string}=$proxy_val"

		# If APTGET_HOST_PROXIES is not defined in local.conf, then
		# apt.conf is populated using proxy information in ENV_HOST_PROXIES
		if [ -z "${APTGET_HOST_PROXIES}" ]; then
			echo >>"${APTGET_CHROOT_DIR}/etc/apt/apt.conf" "Acquire::$proxy_type::proxy \"$proxy_val/\"; /* Yocto */"
		fi
	done

	# With this little trick, we can qemu target-side executables
	# inside pseudo chroot without losing pseudo functionality.
	# This is a must have for some of the package related scripts
	# that have to use the target side executables.
	# This depends on both our pseudo and qemu update
	export PSEUDO_CHROOT_XPREFIX="${PSEUDO_CHROOT_XPREFIX}"
	export QEMU_SET_ENV
	export QEMU_UNSET_ENV="${QEMU_UNSET_ENV}"
	export QEMU_LIBCSYSCALL="1"
	#unset QEMU_LD_PREFIX

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
	if [ -e "${APTGET_DL_CACHE}" ]; then
		mkdir -p "${APTGET_CACHE_DIR}"
		chroot "${APTGET_CHROOT_DIR}" /usr/bin/apt-get -qy check
		rsync -v -d -u -t --include *.deb "${APTGET_DL_CACHE}/" "${APTGET_CACHE_DIR}"
		chroot "${APTGET_CHROOT_DIR}" /usr/bin/apt-get -qy check
	fi

	# Before we can play with the package manager in any
	# meaningful way, we need to sync the database.
	if [ -n "${APTGET_EXTRA_SOURCE_PACKAGES}" ]; then
		if grep '# deb-src' ${APTGET_CHROOT_DIR}/etc/apt/sources.list; then
			chroot "${APTGET_CHROOT_DIR}" /bin/sed -i 's/# deb-src/deb-src/g' /etc/apt/sources.list
		fi
	fi

	# Prepare apt to be generically usable
	chroot "${APTGET_CHROOT_DIR}" /usr/bin/apt-get -qy update
	if [ -n "${APTGET_INIT_PACKAGES}" ]; then
		x="${APTGET_INIT_PACKAGES}"
		chroot "${APTGET_CHROOT_DIR}" /usr/bin/apt-get -qy install $x
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
			chroot "${APTGET_CHROOT_DIR}" /usr/bin/apt-get -qy install curl gnupg2
		else
			chroot "${APTGET_CHROOT_DIR}" /usr/bin/apt-get -qy install gnupg2 dirmngr
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
		chroot "${APTGET_CHROOT_DIR}" /usr/bin/apt-get -qy update
	fi

	if [ "${APTGET_SKIP_UPGRADE}" = "0" ]; then
		chroot "${APTGET_CHROOT_DIR}" /usr/bin/apt-get -qyf install
		chroot "${APTGET_CHROOT_DIR}" /usr/bin/apt-get -qy upgrade
	fi

	if [ "${APTGET_SKIP_FULLUPGRADE}" = "0" ]; then
		chroot "${APTGET_CHROOT_DIR}" /usr/bin/apt-get -qyf install
		chroot "${APTGET_CHROOT_DIR}" /usr/bin/apt-get -qy full-upgrade
	fi

	if [ -n "${APTGET_EXTRA_PACKAGES_SERVICES_DISABLED}" ]; then
		# workaround - deny (re)starting of services, for selected packages, since
		# they will make the installation fail
		echo  >"${APTGET_CHROOT_DIR}/usr/sbin/policy-rc.d" "#!/bin/sh"
		echo >>"${APTGET_CHROOT_DIR}/usr/sbin/policy-rc.d" "exit 101"
		chmod a+x "${APTGET_CHROOT_DIR}/usr/sbin/policy-rc.d"

		chroot "${APTGET_CHROOT_DIR}" /usr/bin/apt-get -q -y install ${APTGET_EXTRA_PACKAGES_SERVICES_DISABLED}

		# remove the workaround
		rm -rf "${APTGET_CHROOT_DIR}/usr/sbin/policy-rc.d"
	fi

	if [ -n "${APTGET_EXTRA_PACKAGES}" ]; then
		chroot "${APTGET_CHROOT_DIR}" /usr/bin/apt-get -qy install ${APTGET_EXTRA_PACKAGES}
	fi

	if [ -n "${APTGET_EXTRA_SOURCE_PACKAGES}" ]; then
		# We need this to get source package handling properly
		# configured for a subsequent apt-get source
		chroot "${APTGET_CHROOT_DIR}" /usr/bin/apt-get -qy install dpkg-dev

		# For lack of a better idea, we install source packages
		# into the root user's home. if we could guarantee that
		# they are all read only, /opt might be a good place.
		# But we can't guarantee that.
		# Net result is that we use an ugly hack to overcome
		# the chroot directory problem.
		echo  >"${APTGET_CHROOT_DIR}/aptgetsource.sh" "#!/bin/sh"
		echo >>"${APTGET_CHROOT_DIR}/aptgetsource.sh" "cd \$1"
		echo >>"${APTGET_CHROOT_DIR}/aptgetsource.sh" "/usr/bin/apt-get -yq source \$2"
		x="${APTGET_EXTRA_SOURCE_PACKAGES}"
		for i in $x; do
			chroot "${APTGET_CHROOT_DIR}" /bin/bash /aptgetsource.sh "/root" "${i}"
		done
		rm -f "${APTGET_CHROOT_DIR}/aptgetsource.sh"
	fi

	# The list of installed packages goes into the log
        echo "Installed packages:"
	chroot "${APTGET_CHROOT_DIR}" /usr/bin/dpkg -l | grep '^ii' | awk '{print $2}'

	set +x
}

# empty placeholder, override it in parent script for more functionality
fakeroot do_shell_update() {
	:
}

fakeroot do_shell_update_append() {

	set -x

	if [ -n "${APTGET_EXTRA_PACKAGES_LAST}" ]; then
		chroot "${APTGET_CHROOT_DIR}" /usr/bin/apt-get -qy install ${APTGET_EXTRA_PACKAGES_LAST}
	fi

	# Once we have done the installation, save off the package
	# cache locally for repeated use of recipe building
	if [ -e "${APTGET_CACHE_DIR}" ]; then
		mkdir -p "${APTGET_DL_CACHE}"
		rsync -v -d -u -t --include *.deb "${APTGET_CACHE_DIR}/" "${APTGET_DL_CACHE}" 
		chroot "${APTGET_CHROOT_DIR}" /usr/bin/apt-get -qy check
	fi

	if [ "${APTGET_SKIP_CACHECLEAN}" = "0" ]; then
		chroot "${APTGET_CHROOT_DIR}" /usr/bin/apt-get -qy clean
	fi

	# Delete any temp proxy lines we may have added in the target rootfs
	if [ -f "${APTGET_CHROOT_DIR}/etc/apt/apt.conf" ]; then
		sed -i '/^Acquire::.+; \/* Yocto *\/\s*$/d' "${APTGET_CHROOT_DIR}/etc/apt/apt.conf"
	fi

	set +x
}

# The various apt packages need to be translated properly into Yocto
# RPROVIDES_${PN}
APTGET_ALL_PACKAGES = "${APTGET_EXTRA_PACKAGES} \
	${APTGET_EXTRA_PACKAGES_LAST} \
	${APTGET_EXTRA_SOURCE_PACKAGES} \
	${APTGET_EXTRA_PACKAGES_SERVICES_DISABLED} \
	${APTGET_RPROVIDES} \
"
python () {
	pn = (d.getVar('PN', True) or "")
	packagelist = (d.getVar('APTGET_ALL_PACKAGES', True) or "").split()
	translations = (d.getVar('APTGET_YOCTO_TRANSLATION', True) or "").split()

	rprovides = (d.getVar('RPROVIDES_%s' % pn, True) or "").split()
	for p in packagelist:
		appendp = True
		for t in translations:
			pkg,yocto = t.split(":")
			if p == pkg:
				for i in yocto.split(","):
					if i not in rprovides:
						bb.debug(1, 'Adding RPROVIDES_%s = "%s"' % (pn, i))
						rprovides.append(i)
						appendp = False
		if appendp:
			rprovides.append(p)
	if rprovides:
		d.setVar('RPROVIDES_%s' % pn, ' '.join(rprovides))
}
