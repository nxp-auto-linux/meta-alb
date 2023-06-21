#
# Generically build a ubuntu-base package set.
#

inherit nativeaptinstall

APTGET_CHROOT_DIR = "${D}"

S = "${WORKDIR}/rootfs"
B = "${S}"

UBUNTU_CDIMAGE_LOC ?= "cdimage.ubuntu.com/ubuntu-base/releases/${UBUNTU_TARGET_BASEVERSION}/release"
UBUNTU_ROOTFS ?= "ubuntu-base-${UBUNTU_TARGET_VERSION}-base-${UBUNTU_TARGET_ARCH}.tar.gz"
SRC_URI = " \
	http://${UBUNTU_CDIMAGE_LOC}/${UBUNTU_ROOTFS};unpack=0;subdir=rootfs \
	file://fstab \
"

ROOTFS = "${@d.getVar('UBUNTU_ROOTFS',True).split(';')[0]}"

# Note that we still populate_sysroot because otherwise manifest
# updates through staging would not happen.
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_populate_sdk[noexec] = "1"

# As this package will never be built/compiled, we do not really need
# to look at any build- or runtime-dependencies or debug symbols!
# We have quite a number of deficiencies to ignore. The Ubuntu packages
# and rootfs do not really conform to Yocto rules.
# empty-dirs is a special case because we are doing things right here
# and kirkstone gets it wrong by following softlinks and then seeing
# host side files.
INSANE_SKIP:${PN} += "already-stripped build-deps file-rdeps dev-so staticdev mime empty-dirs"
INHIBIT_DEFAULT_DEPS = "1"

# We need to keep all the other recipes happy that will rely on us
# during runtime through default dependencies!
RCONFLICTS:${PN} += "glibc eglibc"
RREPLACES:${PN} += "glibc eglibc"

# The basic ubuntu-base rootfs does not provide enough functionality to
# enable ethernet or interfaces automatically in general.
# We support ifconfig, route, busybox, udhcpc
# which in conjunction should be enough to start up the Ethernet and
# do a local apt-get install of major packages.
APTGET_EXTRA_PACKAGES += "\
	udev \
	sudo udhcpc htop iproute2 \
	whiptail \
	kmod gnupg net-tools netbase \
"
# We *MUST* provide all Ubuntu packages in ubuntu-base that we need
# to install Yocto packages! Packages installed in images via
# IMAGE_INSTALL will not help to resolve the dependency chain!
# The following is an incomplete hint at dependency needs to accomodate
# Yocto packages:
# - dtc, xz-utils: LS2 restool references libs
# - python*, bc, db*: kernel-devsrc has scripts
# Note how we translate Ubuntu names into Yocto names for proper
# handling.
APTGET_EXTRA_PACKAGES += "\
    bison \
    device-tree-compiler \
    libnss-db \
    libssl-dev \
    openssl \
	python${APTGET_PYTHON_VER} bc db5.3 \
	xz-utils bzip2 gawk \
"
# Some packages are implicitly present in Ubuntu due to the kernel
# and rootfs configuration. We pretend to have the respective Yocto
# names therefore. Stuff is grouped for easier updates
RPROVIDES:${PN}:ubuntu += " \
/bin/sh \
flex \
"
RPROVIDES:${PN}:ubuntu += " \
shared-mime-info \
"
RPROVIDES:${PN}:ubuntu += " \
rtld(GNU_HASH) \
libc6 \
libc6-utils \
libc6-extra-nss \
libc6-thread-db \
libc6-pcprofile \
libcrypto1.1 \
libgcc1-dev \
libssl1.1 \
update-alternatives \
"
RPROVIDES:${PN}:ubuntu += " \
pam-plugin-access \
pam-plugin-debug \
pam-plugin-deny \
pam-plugin-echo \
pam-plugin-env \
pam-plugin-exec \
pam-plugin-faildelay \
pam-plugin-filter \
pam-plugin-ftp \
pam-plugin-group \
pam-plugin-issue \
pam-plugin-keyinit \
pam-plugin-limits \
pam-plugin-lastlog \
pam-plugin-listfile \
pam-plugin-localuser \
pam-plugin-loginuid \
pam-plugin-mkhomedir \
pam-plugin-motd \
pam-plugin-namespace \
pam-plugin-nologin \
pam-plugin-permit \
pam-plugin-pwhistory \
pam-plugin-rhosts \
pam-plugin-rootok \
pam-plugin-securetty \
pam-plugin-shells \
pam-plugin-stress \
pam-plugin-succeed-if \
pam-plugin-time \
pam-plugin-timestamp \
pam-plugin-tally \
pam-plugin-tally2 \
pam-plugin-umask \
pam-plugin-unix \
pam-plugin-warn \
pam-plugin-wheel \
pam-plugin-xauth \
"
RPROVIDES:${PN}:ubuntu += " \
libform5 \
libformw5 \
libmenu5 \
libmenuw5 \
libncurses5 \
libncursesw5 \
libpanel5 \
libpanelw5 \
libtic5 \
libtinfo5 \
ncurses-terminfo-base \
glibc-utils \
"

# kernel-devsrc relies on some of these, but we can't pull in the Yocto
# versions. We need to install the corresponding Ubuntu versions.
RPROVIDES:${PN}:ubuntu += " \
gcc \
gcc-plugins \
gcc-runtime \
libmpc-dev \
python3-core \
"

# A few things are provided where we don't have a Yocto equivalent
# currently/possibly. We add them preemptively just in case
RPROVIDES:${PN}:ubuntu += " \
pam-plugin-tty-audit \
pam-plugin-userdb \
pam-plugin-extrausers \
pam-plugin-sepermit \
pam-plugin-selinux \
"

# Ensure that all files of the tgz end up in our packages
FILES:${PN} += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/lib*${SOLIBS}"
FILES:${PN} += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/ld*${SOLIBS}"
FILES:${PN}-dev = ""
#FILES:${PN}-dev += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/lib*${SOLIBSDEV}"
#FILES:${PN}-dev += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/ld*${SOLIBSDEV}"
#FILES:${PN}-dev += "${libdir}/sudo/lib*${SOLIBSDEV}"
FILES:${PN}-dbg += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/.debug/lib*${SOLIBS}"
FILES:${PN}-dbg += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/.debug/lib*${SOLIBSDEV}"

FILES:${PN} += "/usr/include"
FILES:${PN} += "/usr/share /usr/lib /usr/local /usr/games /usr/src"
FILES:${PN} += "/dev"
FILES:${PN} += "/lib"
FILES:${PN} += "/run /dev /lib /media /boot /tmp /proc /opt /mnt /home /srv /sys /run /root"
FILES:${PN}-dbg:append = " ${localstatedir}/lib/dpkg/info/.debug"
FILES:${PN}-dbg:${TRANSLATED_TARGET_ARCH} += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/.debug"

fakeroot do_shell_prepare() {
	cd "${D}"
	rm -rf "*"
	tar -C "${D}" -xzf "${S}/${ROOTFS}"
}

fakeroot do_aptget_user_update:append() {

	# Without an /etc/fstab, the rootfs remains read only
	install -m 0644 ${WORKDIR}/fstab ${D}${sysconfdir}/fstab

	# The info dir file causes a Yocto complaint.
	# Per Yocto docs it should never be packaged
	rm -f "${APTGET_CHROOT_DIR}${infodir}/dir"

	# The default ubuntu-base rootfs does not do filesystem
	# fixes on boot. Given the nature of the BlueBox, we want
	# to enable that by default
	if [ -f "${APTGET_CHROOT_DIR}/lib/init/vars.sh" ]; then
		sed -i "s/^#*FSCKFIX\s*=.*/FSCKFIX=yes/g" "${APTGET_CHROOT_DIR}/lib/init/vars.sh"
	fi
}

do_install[network] = "1"
python do_install() {
    bb.build.exec_func("do_shell_prepare", d)
    bb.build.exec_func("do_aptget_update", d)
}

PKGDEST = "${WORKDIR}/packages-split"
fakeroot python prep_xattr_postinst() {
    # Tricky hack. We go through the dirs and files of the split
    # package to determine if there is any non-root uid/gid to preserve.
    # if so, we generate a postinst script that should do just that.
    # We then reset the ownership to root so that on an rpm install
    # there won't be any warnings about unknown users or groups.
    # We unfortunately cannot use the useradd.bbclass because it uses
    # a static user/group definition whereas we need to pull it from
    # the actual /etc/(passwd|group) information, and it also uses
    # tools that are not compatible to the Ubuntu rootfs tools at this
    # time. While fixing this would be nice, it takes a good amount of
    # work to understand how this might be achieved.
    pkgd = d.getVar('PKGD', True)
    pkgdest = d.getVar('PKGDEST', True)
    packages = d.getVar('PACKAGES', True).split()
    for pkg in packages:
        lines = []
        pkgdpath = os.path.join(pkgd, "")
        pkgdestpath = os.path.join(pkgdest, pkg)
        for basedir, dirnames, filenames in os.walk(pkgdestpath):
            basedirchroot = basedir[ len(pkgdestpath) : ]
            for f in dirnames + filenames:
                ff = os.path.join(basedir, f)
                s = os.lstat(ff)
                if s.st_uid != 0 or s.st_gid != 0:
                    lines.append(str(s.st_uid) + ":" + str(s.st_gid) + ' "' + os.path.join(basedirchroot, f) + '"')
                    os.chown(ff, 0, 0)
                    ffd = os.path.join(pkgd + basedirchroot, f)
                    os.chown(ffd, 0, 0)
                    s = os.lstat(ffd)
                    if s.st_uid != 0 or s.st_gid != 0:
                        bb.error("Can't reset UID/GID for %s!" % ff)

        if lines:
            prefix = '#!/bin/sh -e\n' \
                 'if [ x"$D" = "x" ]; then\n' \ 
                 '    # Do nothing on target!\n' \
                 '    exit 1\n' \
                 'else\n'
            body = ''
            for l in lines:
                body += 'chroot "$D" chown ' + l + '\n'
            suffix = 'fi\n'

            d.setVar('pkg_postinst:' + pkg, prefix + body + suffix)
}
PACKAGEFUNCS =+ "prep_xattr_postinst"

COMPATIBLE_MACHINE = "ubuntu"
