# Copyright 2022 NXP

SUMMARY = "QSPI Flash Linux Script Utilitary"
LICENSE = "BSD-3-Clause"

S = "${WORKDIR}"
FLASH_SCRIPT_FILE = "s32cc_qspi_write.sh"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"
SRC_URI = "file://${FLASH_SCRIPT_FILE}"

RDEPENDS:${PN} += "bash gawk mtd-utils"

do_install() {
	install -d ${D}/opt/
	install -m 0644 ${FLASH_SCRIPT_FILE} ${D}/opt/

	chmod +x ${D}/opt/${FLASH_SCRIPT_FILE}
}

FILES:${PN} += "/opt/${FLASH_SCRIPT_FILE}"
