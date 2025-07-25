# Copyright 2025 NXP

SUMMARY = "Firmware binaries for DDR"
LICENSE = "NXP-EULA"
LIC_FILES_CHKSUM = "file://NXP-EULA;md5=89eeb8bd05ad505c34569139dc4ae495"
NO_GENERIC_LICENSE[NXP-EULA] = "NXP-EULA"
FILESEXTRAPATHS:prepend := "${NXP_FIRMWARE_LOCAL_DIR}:"

DDR_LICENSE_FILE = "NXP-EULA"

DDR_FW_DIR = "${TFA_PLAT}"
DDR_FW_BIN_ECC_OFF ?= "ddr_fw_ecc_off.bin"
DDR_FW_BIN_ECC_ON ?= "ddr_fw_ecc_on.bin"

SRC_URI = " \
    file://${DDR_FW_DIR}/${DDR_FW_BIN_ECC_OFF} \
    file://${DDR_FW_DIR}/${DDR_FW_BIN_ECC_ON} \
    file://${DDR_LICENSE_FILE} \
"

S = "${WORKDIR}"

DDR_FW_INSTALL_DIR ?= "${base_libdir}/ddr-firmware"

do_install() {
    install -d ${D}${DDR_FW_INSTALL_DIR}
    install -m 644 ${S}/${DDR_FW_DIR}/${DDR_FW_BIN_ECC_OFF} ${D}${DDR_FW_INSTALL_DIR}
    install -m 644 ${S}/${DDR_FW_DIR}/${DDR_FW_BIN_ECC_ON} ${D}${DDR_FW_INSTALL_DIR}
}

FILES:${PN} += "${base_libdir}/ddr-firmware"

COMPATIBLE_MACHINE = "s32g|s32r45"
