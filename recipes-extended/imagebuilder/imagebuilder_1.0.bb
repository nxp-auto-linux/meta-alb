# Imagebuilder tool recipe
# Used to generate a u-boot script to ease the configurations that need to
# be made in DT at u-boot runtime for various Xen setups

SUMMARY = "u-boot environment setting tool"
SECTION = "utils"
DEPENDS = "bash"
HOMEPAGE = "https://gitlab.com/ViryaOS/imagebuilder"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

BBCLASSEXTEND = "native"

SRCREV = "6f1950c30cfec0fee35a6c977166148274f037dd"
SRC_URI = "git://gitlab.com/ViryaOS/imagebuilder.git;protocol=https;branch=master"

SRC_URI[sha256sum] = "b4c1d3d482965e9764485fa2eaef5a8e4d03e9fef2c9dcae4752a73309455cf3"

S = "${WORKDIR}/git"
