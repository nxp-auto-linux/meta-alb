# Copyright 2019-2022 NXP

DESCRIPTION = "ARM Trusted Firmware"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://license.rst;md5=1dd070c98a281d18d9eefd938729b031"

DEPENDS += "bc-native"
DEPENDS += "coreutils-native"
DEPENDS += "binutils-native"
DEPENDS += "dtc-native xxd-native"
DEPENDS += "openssl-native"
DEPENDS += "u-boot-s32"
DEPENDS += "u-boot-tools-native"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'optee-os', '', d)}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit deploy

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

# ATF repository
URL ?= "git://source.codeaurora.org/external/autobsps32/arm-trusted-firmware.git;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"
SRCREV ?= "1300336ab0d030497acb34c154347f8b9ca09cee"

SRC_URI += "file://0001-Fix-fiptool-build-error.patch"

BUILD_TYPE = "release"

ATF_BINARIES = "${B}/${ATF_PLAT}/${BUILD_TYPE}"

OPTEE_ARGS = " \
                BL32=${DEPLOY_DIR_IMAGE}/optee/tee-header_v2.bin \
                BL32_EXTRA1=${DEPLOY_DIR_IMAGE}/optee/tee-pager_v2.bin \
                SPD=opteed \
                "

XEN_ARGS = " \
                S32_HAS_HV=1 \
                "

M7BOOT_ARGS = " \
                FIP_OFFSET_DELTA=0x2000 \
                "

EXTRA_OEMAKE += " \
                CROSS_COMPILE=${TARGET_PREFIX} \
                ARCH=${TARGET_ARCH} \
                BUILD_BASE=${B} \
                PLAT=${ATF_PLAT} \
                "
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'optee', '${OPTEE_ARGS}', '', d)}"
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'xen', '${XEN_ARGS}', '', d)}"
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'm7boot', '${M7BOOT_ARGS}', '', d)}"

# FIXME: Allow linking of 'tools' binaries with native libraries
#        used for generating the boot logo and other tools used
#        during the build process.
EXTRA_OEMAKE += 'HOSTCC="${BUILD_CC} ${BUILD_CPPFLAGS}" \
                 HOSTLD="${BUILD_LD} -L${STAGING_BASE_LIBDIR_NATIVE} \
                 -Wl,-rpath,${STAGING_LIBDIR_NATIVE} \
                 -Wl,-rpath,${STAGING_BASE_LIBDIR_NATIVE}" \
                 LIBPATH="${STAGING_LIBDIR_NATIVE}" \
                 HOSTSTRIP=true'

BOOT_TYPE = "sdcard qspi"

do_compile() {
	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS

	oe_runmake -C "${S}" clean

	for suffix in ${BOOT_TYPE}
	do
		oe_runmake -C "${S}" \
		    BL33="${DEPLOY_DIR_IMAGE}/u-boot-nodtb.bin-${suffix}" \
		    MKIMAGE="mkimage" \
		    BL33DIR="${DEPLOY_DIR_IMAGE}/tools/" \
		    MKIMAGE_CFG="${DEPLOY_DIR_IMAGE}/tools/u-boot-s32.cfgout-${suffix}" all
		cp -vf "${ATF_BINARIES}/fip.s32" "${ATF_BINARIES}/fip.s32-${suffix}"
	done
}

do_deploy() {
	install -d ${DEPLOYDIR}
	for suffix in ${BOOT_TYPE}; do
		cp -vf "${ATF_BINARIES}/fip.s32-${suffix}" ${DEPLOYDIR}
	done
}

addtask deploy after do_compile

do_compile[depends] = "virtual/bootloader:do_install"

COMPATIBLE_MACHINE = "s32"
