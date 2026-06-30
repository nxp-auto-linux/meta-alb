SUMMARY = "A prebuilt Ubuntu Base image (24.04 noble) as baseline for custom work"
require ubuntu-license.inc
SECTION = "devel"

# Ubuntu 24.04.4 baseline (arm64)
SRC_URI[sha256sum] = "04207713ece899c3740823d33690441ad3a7f0ded1101aca744e2b0f37ac7ff2"

require ubuntu-base.inc

# Noble uses usrmerge and ships /bin and /sbin as symlinks into /usr
FILES:${PN}:remove = "/lib/udev"
FILES:${PN} += "/bin"
FILES:${PN} += "/sbin"

# Noble's dpkg machinery creates these as sentinel markers indicating
# the rootfs has been merged into /usr. They sit at the root of /
# alongside the /bin -> usr/bin etc. symlinks. Add them to FILES so
# Yocto's do_package QA accepts them.
FILES:${PN} += "/bin.usr-is-merged /sbin.usr-is-merged /lib.usr-is-merged"

# Symlink content that the base tarball does not deliver, but Yocto
# dependency resolution may ask for.
RPROVIDES:${PN}:ubuntu += " \
    /bin/bash \
    /bin/dash \
"

# Installed packages in the pure ubuntu-base 24.04.4 arm64 tarball PLUS
# packages pulled in by APTGET_EXTRA_PACKAGES (apt, sudo, python3.12,
# net-tools, etc.). We list everything as RPROVIDES so that Yocto's
# dependency resolver does not try to install Yocto-built duplicates.
APTGET_RPROVIDES += " \
    apt apt-utils apt-transport-https adduser base-files base-passwd \
    bash bc bison bsdutils busybox bzip2 ca-certificates coreutils \
    dash dbus dbus-user-session debconf debianutils device-tree-compiler \
    diffutils dirmngr distro-info-data dmsetup dpkg e2fsprogs findutils \
    gawk gcc-14-base gnupg gnupg-l10n gnupg-utils gpg gpg-agent \
    gpg-wks-client gpgconf gpgsm gpgv grep gzip hostname htop \
    init-system-helpers iproute2 iputils-ping iso-codes kmod \
    libacl1 libapparmor1 libapt-pkg6.0t64 libargon2-1 libassuan0 libatm1 \
    libattr1 libaudit-common libaudit1 libblkid1 libbpf1 libbrotli1 \
    libbsd0 libbz2-1.0 libc-bin libc6 libcap-ng0 libcap2 libcap2-bin \
    libcom-err2 libcrypt1 libcryptsetup12 libcurl3t64-gnutls libdb5.3t64 \
    libdbus-1-3 libdebconfclient0 libdevmapper1.02.1 libdw1t64 libelf1t64 \
    libexpat1 libext2fs2t64 libfdt1 libffi8 libfribidi0 libgcc-s1 \
    libgcrypt20 libgirepository-1.0-1 libglib2.0-0t64 libglib2.0-data \
    libgmp10 libgnutls30t64 libgpg-error-l10n libgpg-error0 \
    libgssapi-krb5-2 libhogweed6t64 libicu74 libidn2-0 libip4tc2 \
    libjson-c5 libk5crypto3 libkeyutils1 libkmod2 libkrb5-3 \
    libkrb5support0 libksba8 libldap2 liblz4-1 liblzma5 libmd0 libmnl0 \
    libmount1 libmpdec3 libncurses6 libncursesw6 libnettle8t64 \
    libnewt0.52 libnghttp2-14 libnl-3-200 libnl-genl-3-200 libnpth0t64 \
    libnsl2 libnss-systemd libnss-db libp11-kit0 libpam-cap \
    libpam-modules libpam-modules-bin libpam-runtime libpam-systemd \
    libpam0g libpcre2-8-0 libpopt0 libproc2-0 libpsl5t64 \
    libpython3-stdlib libpython3.12-minimal libpython3.12-stdlib \
    libreadline8t64 librtmp1 libsasl2-2 libsasl2-modules \
    libsasl2-modules-db libseccomp2 libselinux1 libsemanage-common \
    libsemanage2 libsepol2 libsigsegv2 libslang2 libsmartcols1 \
    libsqlite3-0 libss2 libssh-4 libssl-dev libssl3t64 libstdc++6 \
    libsystemd0 libtasn1-6 libtinfo6 libtirpc-common libtirpc3t64 \
    libudev1 libunistring5 libuuid1 libxml2 libxtables12 libxxhash0 \
    libyaml-0-2 libzstd1 login logsave mawk media-types mount \
    ncurses-base ncurses-bin net-tools netbase networkd-dispatcher \
    openssl passwd perl perl-base pinentry-curses procps \
    publicsuffix python-apt-common python3 python3-apt python3-blinker \
    python3-cffi-backend python3-cryptography python3-dbus \
    python3-distro python3-gi python3-httplib2 \
    python3-importlib-metadata python3-jeepney python3-jwt \
    python3-keyring python3-launchpadlib python3-lazr.restfulclient \
    python3-lazr.uri python3-minimal python3-more-itertools \
    python3-oauthlib python3-pkg-resources python3-pyparsing \
    python3-secretstorage python3-six python3-software-properties \
    python3-wadllib python3-zipp python3.12 python3.12-minimal \
    readline-common sed sensible-utils shared-mime-info \
    software-properties-common sudo systemd systemd-resolved \
    systemd-sysv systemd-timesyncd sysvinit-utils tar tzdata \
    ubuntu-keyring ucf udev udhcpc unattended-upgrades usrmerge \
    util-linux whiptail xdg-user-dirs xz-utils zlib1g \
"

# Translations from Ubuntu/Debian package names to Yocto names. Noble
# introduced the time_t-64 ABI transition which suffixed many libs
# with 't64'. The Yocto names did not change, so we map them.
APTGET_YOCTO_TRANSLATION += "\
    libdb5.3t64:db \
    libapt-pkg6.0t64:apt \
    libssl3t64:libssl3 \
    libext2fs2t64:libext2fs \
    libgnutls30t64:libgnutls \
    libhogweed6t64:libnettle \
    libnettle8t64:libnettle \
    libnpth0t64:libnpth \
    libtirpc3t64:libtirpc \
    libpsl5t64:libpsl \
    libcurl3t64-gnutls:curl \
    libdw1t64:elfutils \
    libelf1t64:elfutils \
    libglib2.0-0t64:glib-2.0 \
    libreadline8t64:readline \
    libsqlite3-0:sqlite3 \
"

# Yocto splits libc into many gconv subpackages. Map all of them to
# Ubuntu's libc6 so that Yocto's package manager treats them as
# provided by the prebuilt rootfs.
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

# Noble's systemd preinst checks merged-/usr via:
#     [ "$(readlink -f /bin)" = "/usr/bin" ]
# Pseudo's chroot-aware readlink wrapper returns the host path
# (e.g. /opt/work/.../image/usr/bin) instead of /usr/bin, so the
# check fails even though the noble base rootfs IS merged-/usr.
#
# Rather than mutating /usr/bin/readlink in the chroot (which fights
# pseudo's database), use pseudo's PSEUDO_CHROOT_XTRANSLATION mechanism
# to redirect every exec of `readlink` to a host-side wrapper that
# returns the expected answer for the merged-/usr probe and delegates
# everything else to the host's readlink (operating on chroot-prefixed
# paths).
#
# The wrapper is staged from ${WORKDIR} via SRC_URI.
SRC_URI += "file://readlink-merged-usr-wrapper.sh"
PSEUDO_CHROOT_XTRANSLATION:append = ":readlink=${WORKDIR}/readlink-merged-usr-wrapper.sh"
# The wrapper lives outside the chroot rootfs (in WORKDIR). Pseudo's
# PSEUDO_CHROOT_FORCED list matches "/*", so without this exception
# pseudo prepends the chroot prefix and turns the wrapper path into a
# nonexistent path inside the rootfs, defeating the translation.
PSEUDO_CHROOT_EXCEPTIONS:append = ":${WORKDIR}/readlink-merged-usr-wrapper.sh"

fakeroot do_aptget_user_finalupdate:append() {
	# Noble references /usr/local/bin/python in some places. The base
	# rootfs does not actually ship that, so create the symlink to
	# python3 to satisfy any code that looks there.
	chroot "${APTGET_CHROOT_DIR}" ln -sf /usr/bin/python3 /usr/local/bin/python
}
