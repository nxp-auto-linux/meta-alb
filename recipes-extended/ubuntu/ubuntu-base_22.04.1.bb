SUMMARY = "A prebuilt Ubuntu Base image as baseline for custom work"
require ubuntu-license.inc
SECTION = "devel"

# Ubuntu 22.04.1 baseline
SRC_URI[sha256sum] = "b2259205f2e94971e9e146ad1e30925d05d05e6575685cc0125b83746105ec45"

require ubuntu-base.inc

# There are some basic differences between different Ubuntu versions.
# We try not to address them in the generic recipe
APTGET_EXTRA_PACKAGES += ""

# Ubuntu 20 unifies things and turns some things into symlinks. We
# solve this with Yocto "usrmerge" but that isn't quite enough.
# We still need to ship the symlinks.
# We also need to remove the udev reference as apparently bitbake.conf
# isn't quite adapted to usrmerge there.
FILES:${PN}:remove = "/lib/udev"
FILES:${PN} += "/bin"
FILES:${PN} += "/sbin"

# The downside of not having the symlink destination content is that we
# are missing a few basic files that are must have for dependencies.
RPROVIDES:${PN}:ubuntu += " \
    /bin/bash \
    /bin/dash \
"

# We should not have a single PROVIDES entry as this package
# does not provide anything for build time of any other package!
# PROVIDES += ""

# This is the installed package list as found in log_do_install.
# Minor edits have been done to remove an architecture suffix. 
APTGET_RPROVIDES += " \
adduser apt apt-transport-https apt-utils base-files base-passwd bash \
bc bison bsdutils busybox bzip2 ca-certificates coreutils dash \
db5.3-doc db5.3-sql-util db5.3-util dbus dbus-user-session debconf \
debianutils device-tree-compiler diffutils dirmngr distro-info-data \
dmsetup dpkg e2fsprogs findutils gawk gcc-12-base gir1.2-glib-2.0 \
gir1.2-packagekitglib-1.0 gnupg gnupg-l10n gnupg-utils gpg gpg-agent \
gpg-wks-client gpg-wks-server gpgconf gpgsm gpgv grep gzip hostname \
htop init-system-helpers iproute2 iso-codes kmod libacl1 libapparmor1 \
libappstream4 libapt-pkg6.0 libargon2-1 libassuan0 libatm1 libattr1 \
libaudit-common libaudit1 libblkid1 libbpf0 libbrotli1 libbsd0 \
libbz2-1.0 libc-bin libc6 libcap-ng0 libcap2 libcap2-bin libcom-err2 \
libcrypt1 libcryptsetup12 libcurl3-gnutls libdb5.3 libdb5.3++ \
libdb5.3++-dev libdb5.3-dbg libdb5.3-dev libdb5.3-java \
libdb5.3-java-dev libdb5.3-java-jni libdb5.3-sql libdb5.3-sql-dev \
libdb5.3-stl libdb5.3-stl-dev libdb5.3-tcl libdbus-1-3 \
libdebconfclient0 libdevmapper1.02.1 libdw1 libelf1 libexpat1 \
libext2fs2 libfdt1 libffi8 libfribidi0 libgcc-s1 libgcrypt20 \
libgirepository-1.0-1 libglib2.0-0 libglib2.0-bin libglib2.0-data \
libgmp10 libgnutls30 libgpg-error0 libgssapi-krb5-2 libgstreamer1.0-0 \
libhogweed6 libicu70 libidn2-0 libip4tc2 libjson-c5 libk5crypto3 \
libkeyutils1 libkmod2 libkrb5-3 libkrb5support0 libksba8 libldap-2.5-0 \
libldap-common liblz4-1 liblzma5 libmd0 libmnl0 libmount1 libmpdec3 \
libmpfr6 libncurses6 libncursesw6 libnettle8 libnewt0.52 libnghttp2-14 \
libnl-3-200 libnl-genl-3-200 libnpth0 libnsl2 libnss-db libnss-systemd \
libp11-kit0 libpackagekit-glib2-18 libpam-cap libpam-modules \
libpam-modules-bin libpam-runtime libpam-systemd libpam0g libpcre2-8-0 \
libpcre3 libpolkit-agent-1-0 libpolkit-gobject-1-0 libpopt0 libprocps8 \
libpsl5 libpython3-stdlib libpython3.10-minimal libpython3.10-stdlib \
libreadline8 librtmp1 libsasl2-2 libsasl2-modules libsasl2-modules-db \
libseccomp2 libselinux1 libsemanage-common libsemanage2 libsepol2 \
libsigsegv2 libslang2 libsmartcols1 libsqlite3-0 libss2 libssh-4 \
libssl-dev libssl3 libstdc++6 libstemmer0d libsystemd0 libtasn1-6 \
libtcl8.6 libtinfo6 libtirpc-common libtirpc3 libudev1 libunistring2 \
libunwind8 libuuid1 libxml2 libxmlb2 libxtables12 libxxhash0 \
libyaml-0-2 libzstd1 login logsave lsb-base lsb-release m4 mawk \
media-types mount ncurses-base ncurses-bin net-tools netbase \
networkd-dispatcher openssl packagekit packagekit-tools passwd \
perl perl-base pinentry-curses pkexec policykit-1 polkitd procps \
publicsuffix python-apt-common python3 python3-apt python3-blinker \
python3-cffi-backend python3-cryptography python3-dbus python3-distro \
python3-distro-info python3-gi python3-httplib2 \
python3-importlib-metadata python3-jeepney python3-jwt python3-keyring \
python3-launchpadlib python3-lazr.restfulclient python3-lazr.uri \
python3-minimal python3-more-itertools python3-oauthlib \
python3-pkg-resources python3-pyparsing python3-secretstorage \
python3-six python3-software-properties python3-wadllib python3-zipp \
python3.10 python3.10-minimal readline-common sed sensible-utils \
shared-mime-info software-properties-common sudo systemd \
systemd-hwe-hwdb systemd-sysv systemd-timesyncd sysvinit-utils tar tcl \
tcl8.6 tzdata ubuntu-keyring ucf udev udhcpc unattended-upgrades \
usrmerge util-linux whiptail xdg-user-dirs xz-utils zlib1g \
"

# This is a really ugly one for us because Yocto does a very fine
# grained split of libc. Note how we avoid spaces in the wrong places!
APTGET_YOCTO_TRANSLATION += "\
        libc6:libc6,libc6-utils,glibc,eglibc,\
glibc-thread-db,eglibc-thread-db,\
glibc-extra-nss,eglibc-extra-nss,\
glibc-pcprofile,eglibc-pcprofile,\
libsotruss,libcidn,libmemusage,\
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


fakeroot do_aptget_user_finalupdate:append() {
	# Ubuntu 22.04 internally references /usr/local/bin/python
	# which isn't really provided, i.e., the rootfs is inconsistent.
	# While this doesn't pose a problem for building the packages,
	# it is an issue when using them due to the missing reference.
	# So we fake it ...
	chroot "${APTGET_CHROOT_DIR}" ln -s /usr/bin/python3 /usr/local/bin/python
}
