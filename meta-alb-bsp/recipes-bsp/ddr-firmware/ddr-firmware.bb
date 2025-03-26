# Copyright 2025 NXP

SUMMARY = "Firmware binaries for DDR"
LICENSE = "NXP-EULA"
LIC_FILES_CHKSUM = "file://NXP-EULA;md5=a3cc8e4c7b0cbd2f26d689a255d1a98d"
NO_GENERIC_LICENSE[NXP-EULA] = "NXP-EULA"
FILESEXTRAPATHS:prepend := "${NXP_FIRMWARE_LOCAL_DIR}:"

DDR_LICENSE_FILE = "NXP-EULA"

DDR_FW_DIR = "${TFA_PLAT}"
DDR_FW_BIN_ECC_OFF ?= "ddr_fw_ecc_off.bin"
DDR_FW_BIN_ECC_ON ?= "ddr_fw_ecc_on.bin"

SRC_URI = " \
    ${@f"""file://${DDR_FW_DIR}/${DDR_FW_BIN_ECC_OFF}""" if os.path.exists(f"""file://${DDR_FW_DIR}/${DDR_FW_BIN_ECC_OFF}""") else ''} \
    ${@f"""file://${DDR_FW_DIR}/${DDR_FW_BIN_ECC_ON}""" if os.path.exists(f"""file://${DDR_FW_DIR}/${DDR_FW_BIN_ECC_ON}""") else ''} \
    ${@f"""file://${DDR_LICENSE_FILE}""" if os.path.exists(f"""file://${DDR_LICENSE_FILE}""") else ''} \
"

S = "${WORKDIR}"

DDR_FW_INSTALL_DIR ?= "${base_libdir}/ddr-firmware"

do_install() {
    install -d ${D}${DDR_FW_INSTALL_DIR}
    install -m 644 ${S}/${DDR_FW_DIR}/${DDR_FW_BIN_ECC_OFF} ${D}${DDR_FW_INSTALL_DIR}
    install -m 644 ${S}/${DDR_FW_DIR}/${DDR_FW_BIN_ECC_ON} ${D}${DDR_FW_INSTALL_DIR}
}

FILES:${PN} += "${base_libdir}/ddr-firmware"

COMPATIBLE_MACHINE = "s32cc"
