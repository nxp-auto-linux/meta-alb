# Copyright 2021 NXP
#
SUMMARY = "Sample M7 Bootloader"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.BSD;md5=0f00d99239d922ffd13cabef83b33444"

URL ?= "git://source.codeaurora.org/external/autobsps32/m7-sample;protocol=https"
BRANCH ?= "${RELEASE_BASE}"
SRC_URI = "${URL};branch=${BRANCH}"
SRCREV ?= "e41226a15c8514df6b3d0c860e3d1b142f9977b1"

S = "${WORKDIR}/git"
BUILD = "${WORKDIR}/build"
BOOT_TYPE = "sdcard qspi"
IVT_FILE_BASE = "${@bb.utils.contains('DISTRO_FEATURES', 'atf', \
	    'fip.s32',\
	    'u-boot-${MACHINE}.s32',\
	    d)}"

do_compile() {
	for suffix in ${BOOT_TYPE}
	do
		ivt_file="${IVT_FILE_BASE}-${suffix}"

		# Update M7 start address in linker file to match the IVT layout
		m7_start_addr=$(./append_m7.sh -i "${DEPLOY_DIR_IMAGE}/${ivt_file}" -e)
		sed -i s/"START_ADDR =.*"/"START_ADDR = ${m7_start_addr};"/g project.lds

		oe_runmake CROSS_COMPILE="arm-none-eabi-" BUILD=${BUILD}-${suffix}
	done
}

do_deploy() {
	install -d ${DEPLOY_DIR_IMAGE}

	for suffix in ${BOOT_TYPE}
	do
		cd "${BUILD}-${suffix}"
		ivt_file="${IVT_FILE_BASE}-${suffix}"

		cp "${DEPLOY_DIR_IMAGE}/${ivt_file}" .
		"${S}/append_m7.sh" -i "${ivt_file}" -b m7.bin -m m7.map
		cp -vf "${ivt_file}.m7" "${DEPLOY_DIR_IMAGE}/"
	done
}

addtask deploy after do_compile

do_compile[depends] += "virtual/bootloader:do_deploy"
do_compile[depends] += "${@bb.utils.contains('DISTRO_FEATURES', 'atf', 'arm-trusted-firmware:do_deploy', '', d)}"
DEPENDS += "cortex-m-toolchain-native"
# hexdump native (used by append_m7.sh) dependency
DEPENDS += "util-linux-native"
