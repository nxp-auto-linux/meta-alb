# Copyright 2021 NXP
#
SUMMARY = "Sample M7 Bootloader"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.BSD;md5=0f00d99239d922ffd13cabef83b33444"

URL ?= "git://source.codeaurora.org/external/autobsps32/m7-sample;protocol=https"
BRANCH ?= "${RELEASE_BASE}"
SRC_URI = "${URL};branch=${BRANCH}"
SRCREV ?= "b8945718e498cb2927a95cfc02b6b23249a6e5bd"

S = "${WORKDIR}/git"
BUILD = "${WORKDIR}/build"
BOOT_TYPE = "sdcard qspi"

EXTRA_OEMAKE += 'CROSS_COMPILE="arm-none-eabi-" BUILD=${BUILD}'

do_deploy() {
	install -d ${DEPLOY_DIR_IMAGE}
	cd ${S}

	for suffix in ${BOOT_TYPE}
	do
		ivt_file="u-boot-${MACHINE}.s32-${suffix}"

		cp "${DEPLOY_DIR_IMAGE}/${ivt_file}" .
		./append_m7.sh "${ivt_file}" "${BUILD}/m7.bin" "${BUILD}/m7.map"
		cp -vf "${ivt_file}.m7" "${DEPLOY_DIR_IMAGE}/"
	done
}

addtask deploy after do_compile

do_deploy[depends] += "virtual/bootloader:do_deploy"
DEPENDS += "cortex-m-toolchain-native"
DEPENDS += "u-boot-s32"
