# Copyright 2023 NXP

SUMMARY = "MMC Linux Script test utilitary"
LICENSE = "BSD-3-Clause"

S = "${WORKDIR}"
MMC_TEST_SCRIPT_FILE = "s32cc_mmc_test_tool.sh"
INSTALL_PATH = "/opt/bsp_utils"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"
SRC_URI = "file://${MMC_TEST_SCRIPT_FILE}"

RDEPENDS:${PN} += "bash"

do_install() {
	install -d ${D}${INSTALL_PATH}
	install -m 0644 ${MMC_TEST_SCRIPT_FILE} ${D}${INSTALL_PATH}

	chmod +x ${D}${INSTALL_PATH}/${MMC_TEST_SCRIPT_FILE}
}

FILES:${PN} += "${INSTALL_PATH}/${MMC_TEST_SCRIPT_FILE}"
