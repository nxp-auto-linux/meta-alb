DESCRIPTION = "libFCI networking acceleration library"
HOMEPAGE = "https://source.codeaurora.org/external/autobsps32/extra/pfeng"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE-GPL2.txt;md5=5dcdfe25f21119aa5435eab9d0256af7"
 
DEPENDS = "pfe"

PR = "r0"
 
SRC_URI = "git://source.codeaurora.org/external/autobsps32/extra/pfeng;protocol=https"
SRCREV = "def922b2caf25f9f8a251d672d092c27244b3bcc"
S = "${WORKDIR}/git"
MDIR = "${S}/sw/linux-pfeng"
LIBBUILDDIR = "${S}/sw/xfci/libfci/build/release/"

CFLAGS_prepend = "-I${S} "
 
PACKAGES = "${PN} ${PN}-dev ${PN}-dbg ${PN}-staticdev"

RDEPENDS_${PN}-staticdev = ""
RDEPENDS_${PN}-dev = ""
RDEPENDS_${PN}-dbg = ""
 
do_compile() {
	cd ${MDIR}
	${MAKE} libfci-build
}
 
do_install() {
	install -d ${D}${libdir}
	install -m 0644 ${LIBBUILDDIR}libfci.a ${D}${libdir}
}
