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

ROOTFS="ubuntu-base-${UBUNTU_TARGET_VERSION}-base-${UBUNTU_TARGET_ARCH}.tar.gz"
SRC_URI = " \
	http://cdimage.ubuntu.com/ubuntu-base/releases/${UBUNTU_TARGET_BASEVERSION}/release/${ROOTFS};unpack=0;subdir=rootfs \
	file://fstab \
"
SRC_URI[md5sum] = "f8013a313d868ed334c17682e2651b32"
SRC_URI[sha256sum] = "aa9771e13631b1b65308027ce5e8d7aa86191e8d38a290d3b8319355fe5093e7"

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
APTGET_EXTRA_PACKAGES += "\
	udev \
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
	xz-utils bzip2 \
	python3.5 python bc db5.3 \
	libffi6 \
"
APTGET_YOCTO_TRANSLATION += "\
	xz-utils:xz,liblzma \
	python3.5:python3 \
	db5.3:db \
	libffi6:libffi \
	zlib1g:libz1 \
	libpam0g:libpam \
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

fakeroot do_aptget_user_update_append() {

	set -x

	# Without an /etc/fstab, the rootfs remains read only
	install -m 0644 ${WORKDIR}/fstab ${D}${sysconfdir}/fstab

	# After we are done installing our extra packages, we
	# optionally kill the log directory content, so that we
	# don't clutter the target
	#rm -rf ${APTGET_CHROOT_DIR}/var/log/*

	# The info dir file causes a Yocto complaint.
	# Per Yocto docs it should never be packaged
	rm -f "${APTGET_CHROOT_DIR}${infodir}/dir"

	# There is an unfulfilled libldap dependency.
	# Fixing the dependency seems a bit tricky to do,
	# so we eliminate the single tool that causes
	# the dependency problem. UNDERSTAND AND FIX!
	#rm -f "${APTGET_CHROOT_DIR}/usr/lib/gnupg/gpgkeys_ldap"

	# The default ubuntu-base rootfs does not do filesystem
	# fixes on boot. Given the nature of the BlueBox, we want
	# to enable that by default
	if [ -f "${APTGET_CHROOT_DIR}/lib/init/vars.sh" ]; then
		sed -i "s/^#*FSCKFIX\s*=.*/FSCKFIX=yes/g" "${APTGET_CHROOT_DIR}/lib/init/vars.sh"
	fi

	# Add /usr/bin/python3 symlink to /usr/bin/python3.5, as it is required
	# by other packages depending on python3
	if [ ! -f ${APTGET_CHROOT_DIR}/usr/bin/python3 ]; then
		cd ${APTGET_CHROOT_DIR}/usr/bin/
		ln -s python3.5 python3
		cd -
	fi
	set +x
}

python do_install() {
    bb.build.exec_func("do_shell_prepare", d)
    bb.build.exec_func("do_aptget_update", d)
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
bzip2 \
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
python3 \
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
