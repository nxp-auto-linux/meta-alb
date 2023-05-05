# Copyright 2022 NXP

SUMMARY = "QSPI Flash Linux Script Utilitary"
LICENSE = "BSD-3-Clause"

S = "${WORKDIR}"
FLASH_SCRIPT_FILE = "s32cc_qspi_write.sh"
INSTALL_PATH = "/opt/bsp_utils"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"
SRC_URI = "file://${FLASH_SCRIPT_FILE}"

RDEPENDS:${PN} += "bash gawk mtd-utils"

do_install() {
	install -d ${D}${INSTALL_PATH}
	install -m 0644 ${FLASH_SCRIPT_FILE} ${D}${INSTALL_PATH}

	chmod +x ${D}${INSTALL_PATH}/${FLASH_SCRIPT_FILE}
}

FILES:${PN} += "${INSTALL_PATH}/${FLASH_SCRIPT_FILE}"
