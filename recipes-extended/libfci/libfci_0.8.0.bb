DESCRIPTION = "libFCI networking acceleration library"
HOMEPAGE = "https://source.codeaurora.org/external/autobsps32/extra/pfeng"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=116de28c37181b72b36566106a941904"
 
DEPENDS = "pfe"

PR = "r0"
 
SRC_URI = "git://source.codeaurora.org/external/autobsps32/extra/pfeng;protocol=https"
SRCREV = "9236fe31e2e33d5c437eabf66838f5616d38b0e6"
S = "${WORKDIR}/git/"
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
