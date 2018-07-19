SUMMARY = "A prebuilt Ubuntu Base image as baseline for custom work"
require ubuntu-license.inc
SECTION = "devel"

inherit nativeaptinstall

APTGET_CHROOT_DIR = "${D}"

S = "${WORKDIR}/rootfs"
B = "${S}"

# I am sure there are smarter ways to map the architecture. This works
# initially. FIX
UBUNTU_TARGET_ARCH="${@d.getVar('TARGET_ARCH', True).replace("aarch64", "arm64")}"

ROOTFS="ubuntu-base-16.04.3-base-${UBUNTU_TARGET_ARCH}.tar.gz"
SRC_URI = " \
	http://cdimage.ubuntu.com/ubuntu-base/releases/16.04/release/${ROOTFS};unpack=0;subdir=rootfs \
	file://fstab \
"
SRC_URI[md5sum] = "a01899507180fbdb6caca334057ad086"
SRC_URI[sha256sum] = "76553b0c5153d8f69f2ab6a60a8e9129ff87be5e9c4d2f1791ce58a84ad08b6c"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_populate_sysroot[noexec] = "1"
do_populate_sdk[noexec] = "1"

# As this package will never be built/compiled, we do not really need
# to look at any build- or runtime-dependencies or debug symbols!
# We have quite a number of deficiencies to ignore. The Ubuntu packages
# and rootfs do not really conform to Yocto rules.
INSANE_SKIP_${PN} += "already-stripped build-deps file-rdeps dev-so staticdev"
INHIBIT_DEFAULT_DEPS = "1"

# The basic ubuntu-base rootfs does not provide enough functionality to
# enable ethernet or interfaces automatically in general.
# We support ifconfig, route, busybox, udhcpc
# which in conjunction should be enough to start up the Ethernet and
# do a local apt-get install of major packages.
APTGET_EXTRA_PACKAGES = "\
	apt-utils udev \
	sudo udhcpc htop iproute2 \
	whiptail \
	kmod gnupg net-tools netbase \
"
# The following is an incomplete hint at dependency needs to accomodate
# Yocto packages:
# - xz-utils: LS2 restool references libs
# - python*, bc, db*: kernel-devsrc has scripts
# Note how we translate Ubuntu names into Yocto names for proper
# handling.
APTGET_EXTRA_PACKAGES += "\
	xz-utils \
	python3.5 python bc db5.3 \
	libffi6 \
"
APTGET_YOCTO_TRANSLATION = "\
	xz-utils:xz,liblzma \
	python3.5:python3 \
	db5.3:db \
	libffi6:libffi \
"

# We do not want a local glibc used to build local Yocto packages to
# interfere with what we provide in ubuntu-base. Therefore we override
# any .so reference in shared libs with an explicit reference to our
# package in our version and eliminate all other references by prefixing
# with "*/" to ours.
# NOTE: Updated package.bbclass required for path support!
require ubuntu-tarfiles.inc
LOCALONLY_SHLIBS = "${TARFILES}"
python __anonymous () {
    import re
    aslibs = []
    pn = d.getVar('PN', True)
    lib_re = re.compile("^.*\.so")
    for i in (d.getVar('LOCALONLY_SHLIBS', True) or "").split():
        if lib_re.match(i):
            aslibs.append('*/' + os.path.basename(i) + ':' + pn)

    bb.debug(2, "Adding to ASSUME_SHLIBS: {}".format(aslibs))
    a_s = d.getVar('ASSUME_SHLIBS', True) or ""
    a_s += ' '.join(aslibs)
    d.setVar('ASSUME_SHLIBS', a_s)
}

# Ensure that all files of the tgz end up in our packages
FILES_${PN} += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/lib*${SOLIBS}"
FILES_${PN} += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/ld*${SOLIBS}"
FILES_${PN}-dev = ""
#FILES_${PN}-dev += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/lib*${SOLIBSDEV}"
#FILES_${PN}-dev += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/ld*${SOLIBSDEV}"
#FILES_${PN}-dev += "${libdir}/sudo/lib*${SOLIBSDEV}"
FILES_${PN}-dbg += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/.debug/lib*${SOLIBS}"
FILES_${PN}-dbg += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/.debug/lib*${SOLIBSDEV}"

FILES_${PN} += "/usr/include"
FILES_${PN} += "/usr/share /usr/lib /usr/local /usr/games /usr/src"
FILES_${PN} += "/dev"
FILES_${PN} += "/lib"
FILES_${PN} += "/run /dev /lib /media /boot /tmp /proc /opt /mnt /home /srv /sys /run /root"
FILES_${PN}-dbg_append = " ${localstatedir}/lib/dpkg/info/.debug"
FILES_${PN}-dbg_${TRANSLATED_TARGET_ARCH} += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/.debug"

fakeroot do_shell_prepare() {
	cd "${D}"
	rm -rf "*"
	tar -C "${D}" -xzf "${S}/${ROOTFS}"
}

fakeroot do_shell_update() {

	set -x

	# Without an /etc/fstab, the rootfs remains read only
	install -m 0644 ${WORKDIR}/fstab ${D}${sysconfdir}/fstab

	# After we are done installing our extra packages, we
	# optionally kill the log directory content, so that we
	# don't clutter the target
	#rm -rf ${APTGET_CHROOT_DIR}/var/log/*

	# The info file causes a Yocto complaint.
	# It may be easier to recreate it later. UNDERSTAND AND FIX!
	rm -f "${APTGET_CHROOT_DIR}/usr/share/info/dir"

	# There is an unfulfilled libldap dependency.
	# Fixing the dependency seems a bit tricky to do,
	# so we eliminate the single tool that causes
	# the dependency problem. UNDERSTAND AND FIX!
	rm -f "${APTGET_CHROOT_DIR}/usr/lib/gnupg/gpgkeys_ldap"

	# The default ubuntu-base rootfs does not do filesystem
	# fixes on boot. Given the nature of the BlueBox, we want
	# to enable that by default
	sed -i "s/^#*FSCKFIX\s*=.*/FSCKFIX=yes/g" "${APTGET_CHROOT_DIR}/etc/default/rcS"

	set +x
}

python do_install() {
    bb.build.exec_func("do_shell_prepare", d)
    bb.build.exec_func("do_shell_update", d)
}

PKGDEST = "${WORKDIR}/packages-split"
python prep_xattr_postinst() {
    # Tricky hack. We go through the dirs and files of the split
    # package to determine if there is any non-root uid/gid to preserve.
    # if so, we generate a postinst script that should do just that.
    pkgdest = d.getVar('PKGDEST', True)
    packages = d.getVar('PACKAGES', True).split()
    for pkg in packages:
        lines = []
        pkgpath = os.path.join(pkgdest, pkg)
        for basedir, dirnames, filenames in os.walk(pkgpath):
            basedirchroot = basedir[ len(pkgpath) : ]
            for f in dirnames + filenames:
                ff = os.path.join(basedir, f)
                s = os.lstat(ff)
                if s.st_uid != 0 or s.st_gid != 0:
                    lines.append(str(s.st_uid) + ":" + str(s.st_gid) + ' "' + os.path.join(basedirchroot, f) + '"')

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

            d.setVar('pkg_postinst_' + pkg, prefix + body + suffix)
}
PACKAGEFUNCS =+ "prep_xattr_postinst"

COMPATIBLE_MACHINE = "ubuntu"

# We should not have a single PROVIDES entry as this package
# does not provide anything for build time of any other package!
# PROVIDES += ""

RPROVIDES_${PN}_ubuntu += "\
	eglibc rtld(GNU_HASH) \
"

#
RPROVIDES_${PN}_ubuntu += "\
	base-files \
	bash \
	libc6 \
	glibc \
	libgcc1-dev \
"
#
RPROVIDES_${PN}_ubuntu += "\
	libattr1 \
	libcap2 \
	libblkid1 \
	libfdisk1 \
	libffi6 \
	libmount1 \
	libncurses5 \
	libncursesw5 \
	libpam \
	libpanelw5 \
	libsmartcols1 \
	libtic5 \
	libtinfo5 \
	libuuid1 \
	libz1 \
	ncurses-terminfo-base \
	netbase \
	pam-plugin-deny \
	pam-plugin-permit \
	pam-plugin-unix \
	pam-plugin-warn \
	update-alternatives \
"

# The content from the original Ubuntu manifest file.
# This does not precisely correlate to the package names we have,
# but it is a good start.
RPROVIDES_${PN}_ubuntu += "\
adduser \
alsa-conf \
apt \
base-files \
base-passwd \
bash \
bsdutils \
coreutils \
dash \
debconf \
debianutils \
diffutils \
dpkg \
e2fslibs \
e2fsprogs \
findutils \
gcc-5-base \
gcc-6-base \
gnupg \
gpgv \
grep \
gzip \
hostname \
init \
init-system-helpers \
initscripts \
insserv \
libacl1 \
libapparmor1 \
libapt-pkg5.0 \
libasound2 \
libattr1 \
libaudit-common \
libaudit1 \
libblkid1 \
libbz2-1.0 \
libbz2.so.1 \
libc-bin \
libc6 \
libcap2 \
libcap2-bin \
libcomerr2 \
libcryptsetup4 \
libdb5.3 \
libdebconfclient0 \
libdevmapper1.02.1 \
libfdisk1 \
libgcc1 \
libgcrypt20 \
libgpg-error0 \
libkmod2 \
liblz4-1 \
liblzma5 \
libmount1 \
libncurses5 \
libncursesw5 \
libpam-modules \
libpam-modules-bin \
libpam-runtime \
libpam0g \
libpcre3 \
libprocps4 \
libreadline6 \
libseccomp2 \
libselinux1 \
libsemanage-common \
libsemanage1 \
libsepol1 \
libsmartcols1 \
libss2 \
libstdc++6 \
libsystemd0 \
libtinfo5 \
libudev1 \
libusb-0.1-4 \
libustr-1.0-1 \
libuuid1 \
locales \
login \
lsb-base \
makedev \
mawk \
mount \
multiarch-support \
ncurses-base \
ncurses-bin \
passwd \
perl-base \
procps \
readline-common \
sed \
sensible-utils \
systemd \
systemd-sysv \
sysv-rc \
sysvinit-utils \
tar \
tzdata \
ubuntu-keyring \
util-linux \
zlib1g \
"
