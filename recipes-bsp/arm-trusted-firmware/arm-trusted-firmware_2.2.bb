# Copyright 2019-2020 NXP

DESCRIPTION = "ARM Trusted Firmware"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://license.rst;md5=1dd070c98a281d18d9eefd938729b031"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

# ATF repository
SRC_URI = "git://source.codeaurora.org/external/autobsps32/arm-trusted-firmware.git;protocol=https;branch=alb/master"
SRCREV ?= "450067ca44877137c6a2b2c3549039f362ccc8da"

PLATFORM_s32g274aevb = "s32g"
BUILD_TYPE = "release"

EXTRA_OEMAKE += "\
                CROSS_COMPILE=${TARGET_PREFIX} \
                ARCH=${TARGET_ARCH} \
                BUILD_BASE=${B} \
                PLAT=${PLATFORM} \
                "

do_compile() {
        unset LDFLAGS
        oe_runmake -C ${S}
}

do_deploy() {
        install -d ${DEPLOY_DIR_IMAGE}
        cp ${B}/${PLATFORM}/${BUILD_TYPE}/bl2.bin ${DEPLOY_DIR_IMAGE}/bl2-${MACHINE}.bin
        cp ${B}/${PLATFORM}/${BUILD_TYPE}/bl31.bin ${DEPLOY_DIR_IMAGE}/bl31-${MACHINE}.bin
}

addtask deploy before do_build after do_compile

COMPATIBLE_MACHINE = "s32g274aevb"
