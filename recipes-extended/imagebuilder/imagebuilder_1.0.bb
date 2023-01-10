# Imagebuilder tool recipe
# Used to generate a u-boot script to ease the configurations that need to
# be made in DT at u-boot runtime for various Xen setups

SUMMARY = "u-boot environment setting tool"
SECTION = "utils"
RDEPENDS:${PN}:class-native = "bash u-boot-tools-native dtc-native"
RDEPENDS:${PN}:class-target = "u-boot-tools"
HOMEPAGE = "https://gitlab.com/ViryaOS/imagebuilder"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

BBCLASSEXTEND = "native"

FILESEXTRAPATHS:prepend := "${THISDIR}/imagebuilder:"

SRCREV = "0dbdc99cf3accdfdbfe158b99ee0bc9fc71fcdbc"
SRC_URI = "git://gitlab.com/xen-project/imagebuilder.git;protocol=https;branch=master \
    file://0001-uboot-script-gen-Dynamically-compute-addr-and-size-w.patch \
"

SRC_URI[sha256sum] = "b4c1d3d482965e9764485fa2eaef5a8e4d03e9fef2c9dcae4752a73309455cf3"

S = "${WORKDIR}/git"

IMAGEBUILDER_SCRIPT_FILE = "uboot-script-gen"
FILES:${PN} = "${bindir}/${IMAGEBUILDER_SCRIPT_FILE}"

do_install() {
    install -d ${D}/${bindir}
    install -m 0755 ${S}/scripts/${IMAGEBUILDER_SCRIPT_FILE} ${D}${bindir}/
}
