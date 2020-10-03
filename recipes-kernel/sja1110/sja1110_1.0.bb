# Copyright 2020 NXP

SUMMARY = "SJA1110 SPI Driver"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit module

SRC_URI = "git://source.codeaurora.org/external/autoivnsw/sja1110_linux;branch=master;protocol=https"
SRC_URI += "file://0001-s32g274a-rdb2-Add-support-for-S32G-VNP-RDB2.patch"
SRCREV = "ac06f130d512e75edcf98d1a8304edd90055e525"

S = "${WORKDIR}/git"

EXTRA_OEMAKE_append = " KERNELDIR=${KBUILD_OUTPUT} TARGET_ARCH=${ARCH} LOCAL_TOOLCHAIN=${CROSS_COMPILE} LOCAL_COMPILER=${CROSS_COMPILE}gcc"

SJA1110_UC_FW ?= ""
SJA1110_SWITCH_FW ?= ""

module_do_install_append() {
	install -d ${D}/lib/firmware
	if [ -f "${SJA1110_UC_FW}" ]; then
		cp -f ${SJA1110_UC_FW} ${D}/lib/firmware/sja1110_uc.bin
	fi
	if [ -f "${SJA1110_SWITCH_FW}" ]; then
		cp -f ${SJA1110_SWITCH_FW} ${D}/lib/firmware/sja1110_switch.bin
	fi
}

KERNEL_MODULE_AUTOLOAD += "sja1110"

FILES_${PN} += "${base_libdir}/*"
FILES_${PN} += "${sysconfdir}/modules-load.d/*"

PROVIDES = "kernel-module-sja1110${KERNEL_MODULE_PACKAGE_SUFFIX}"
RPROVIDES_${PN} = "kernel-module-sja1110${KERNEL_MODULE_PACKAGE_SUFFIX}"

COMPATIBLE_MACHINE = "s32g274ardb2"
