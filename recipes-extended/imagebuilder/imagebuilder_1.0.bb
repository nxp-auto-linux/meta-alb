# Imagebuilder tool recipe
# Used to generate a u-boot script to ease the configurations that need to
# be made in DT at u-boot runtime for various Xen setups

SUMMARY = "u-boot environment setting tool"
SECTION = "utils"
RDEPENDS:${PN}:class-native = "bash u-boot-tools-native"
RDEPENDS:${PN}:class-target = "u-boot-tools"
HOMEPAGE = "https://gitlab.com/ViryaOS/imagebuilder"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

BBCLASSEXTEND = "native"

FILESEXTRAPATHS:prepend := "${THISDIR}/imagebuilder:"

SRCREV = "6f1950c30cfec0fee35a6c977166148274f037dd"
SRC_URI = "git://gitlab.com/ViryaOS/imagebuilder.git;protocol=https;branch=master \
    file://0001-imagebuilder-Automatically-add-direct-map-and-xen-pa.patch \
    file://0002-imagebuilder-Add-support-for-prepending-path-to-file.patch \
    file://0003-imagebuilder-Disable-autoboot.patch \
    file://0004-imagebuilder-Use-dynamic-sizes-for-loaded-binaries.patch \
    file://0005-imagebuilder-Allow-pasthrough-of-multiple-devices.patch \
    file://0006-imagebuilder-Add-support-for-appending-extra-command.patch \
"

SRC_URI[sha256sum] = "b4c1d3d482965e9764485fa2eaef5a8e4d03e9fef2c9dcae4752a73309455cf3"

S = "${WORKDIR}/git"

IMAGEBUILDER_SCRIPT_FILE = "uboot-script-gen"
FILES:${PN} = "${bindir}/${IMAGEBUILDER_SCRIPT_FILE}"

do_install() {
    install -d ${D}/${bindir}
    install -m 0755 ${S}/scripts/${IMAGEBUILDER_SCRIPT_FILE} ${D}${bindir}/
}
