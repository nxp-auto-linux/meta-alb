DESCRIPTION = "libFCI networking acceleration library"
HOMEPAGE = "https://github.com/nxp-auto-linux/pfeng"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE-GPL2.txt;md5=5dcdfe25f21119aa5435eab9d0256af7"

PR = "r0"

URL ?= "https://github.com/nxp-auto-linux/pfeng;protocol=https"
SRC_URI = "${URL}"
SRCREV = "c76c16e4b17d7fbc9f2fd438db5161da0166b299"
S = "${WORKDIR}/git"
MDIR = "${S}/sw/xfci/libfci"
LIBBUILDDIR = "${S}/sw/xfci/libfci/build/${TARGET_SYS}-release"

CFLAGS_prepend = "-I${S} "

PACKAGES = "${PN}-staticdev"

RDEPENDS_${PN}-staticdev = "pfe"

do_compile() {
	cd ${MDIR}
	${MAKE} TARGET_OS=LINUX PLATFORM=${TARGET_SYS} ARCH=${PACKAGE_ARCH}  linux
}

do_install() {
	install -d ${D}${libdir}
	install -m 0644 ${LIBBUILDDIR}/libfci.a ${D}${libdir}
}

COMPATIBLE_MACHINE = "s32g"
