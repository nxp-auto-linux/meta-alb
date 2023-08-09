# Copyright 2021 NXP
#
SUMMARY = "Sample M7 Bootloader"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.BSD;md5=0f00d99239d922ffd13cabef83b33444"

URL ?= "git://github.com/nxp-auto-linux/m7-sample;protocol=https"
BRANCH ?= "${RELEASE_BASE}"
SRC_URI = "${URL};branch=${BRANCH}"
SRCREV ?= "49a1a2b4754c60415f4a181b1c8a91cebe23efd6"

S = "${WORKDIR}/git"
BUILD = "${WORKDIR}/build"
IVT_FILE_BASE = "fip.s32"

do_compile() {
	for suffix in ${BOOT_TYPE}
	do
		ivt_file="${IVT_FILE_BASE}-${suffix}"

		BDIR="${BUILD}-${suffix}"

		oe_runmake CROSS_COMPILE="arm-none-eabi-" \
			BUILD="${BDIR}" clean

		mkdir -p "${BDIR}"
		cp -vf "${DEPLOY_DIR_IMAGE}/${ivt_file}" "${BDIR}/"

		oe_runmake CROSS_COMPILE="arm-none-eabi-" \
			BUILD="${BDIR}" \
			A53_BOOTLOADER="${BDIR}/${ivt_file}"
	done
}

do_deploy() {
	install -d ${DEPLOY_DIR_IMAGE}

	for suffix in ${BOOT_TYPE}
	do
		BDIR="${BUILD}-${suffix}"
		cd "${BDIR}"
		ivt_file="${IVT_FILE_BASE}-${suffix}"

		cp -vf "${BDIR}/${ivt_file}.m7" "${DEPLOY_DIR_IMAGE}/"
	done
}

addtask deploy after do_compile

do_compile[depends] += "arm-trusted-firmware:do_deploy"
DEPENDS += "cortex-m-toolchain-native"
# hexdump native (used by append_m7.sh) dependency
DEPENDS += "util-linux-native"
