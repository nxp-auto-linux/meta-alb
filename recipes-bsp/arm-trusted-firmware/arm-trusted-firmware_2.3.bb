# Copyright 2019-2021 NXP

DESCRIPTION = "ARM Trusted Firmware"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://license.rst;md5=1dd070c98a281d18d9eefd938729b031"

DEPENDS += "dtc-native xxd-native"
DEPENDS += "openssl-native"
DEPENDS += "u-boot-s32"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'optee-os', '', d)}"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

# ATF repository
URL ?= "git://source.codeaurora.org/external/autobsps32/arm-trusted-firmware.git;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"
SRCREV ?= "530128801d76c0fbbc0b5b41eb52eb4757c78335"

SRC_URI += "file://0001-Fix-fiptool-build-error.patch"

PLATFORM_s32g2 = "s32g2"
BUILD_TYPE = "release"

ATF_BINARIES = "${B}/${PLATFORM}/${BUILD_TYPE}"

OPTEE_ARGS = " \
                BL32=${DEPLOY_DIR_IMAGE}/optee/tee-header_v2.bin \
                BL32_EXTRA1=${DEPLOY_DIR_IMAGE}/optee/tee-pager_v2.bin \
                SPD=opteed \
                "

XEN_ARGS = " \
                S32G_HAS_HV=1 \
                "

EXTRA_OEMAKE += " \
                CROSS_COMPILE=${TARGET_PREFIX} \
                ARCH=${TARGET_ARCH} \
                BUILD_BASE=${B} \
                PLAT=${PLATFORM} \
                "
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'optee', '${OPTEE_ARGS}', '', d)}"
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'xen', '${XEN_ARGS}', '', d)}"

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

	for suffix in ${BOOT_TYPE}
	do
		oe_runmake -C "${S}" \
		    BL33="${DEPLOY_DIR_IMAGE}/u-boot.bin-${suffix}" \
		    MKIMAGE="${DEPLOY_DIR_IMAGE}/tools/mkimage-${suffix}" all
		cp -vf "${ATF_BINARIES}/fip.s32" "${ATF_BINARIES}/fip.s32-${suffix}"
	done
}

do_deploy() {
	install -d ${DEPLOY_DIR_IMAGE}

	for suffix in ${BOOT_TYPE}
	do
		cp -vf "${ATF_BINARIES}/fip.s32-${suffix}" "${DEPLOY_DIR_IMAGE}/"
	done
}

addtask deploy after do_compile

do_compile[depends] = "virtual/bootloader:do_install"

COMPATIBLE_MACHINE = "s32g2"
