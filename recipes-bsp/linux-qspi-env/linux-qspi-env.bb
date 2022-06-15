# Copyright 2022 NXP

SUMMARY = "QSPI Flash Linux environment offsets deployment"
LICENSE = "BSD-3-Clause"

S = "${WORKDIR}"
FLASH_ENV_FILE = "${S}/qspi_env.sh"
FLASH_SCRIPT_FILE = "s32cc_qspi_write.sh"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"
SRC_URI = "file://${FLASH_SCRIPT_FILE}"

RDEPENDS_${PN} += "gawk bash"

do_compile() {
	value="${@d.getVar('FLASHIMAGE_FIP_OFFSET', True)}"
	echo "export FLASHIMAGE_FIP_OFFSET=${value}" > ${FLASH_ENV_FILE}

	value="${@d.getVar('FLASHIMAGE_UBOOT_ENV_OFFSET', True)}"
	echo "export FLASHIMAGE_UBOOT_ENV_OFFSET=${value}" >> ${FLASH_ENV_FILE}

	value="${@d.getVar('FLASHIMAGE_KERNEL_OFFSET', True)}"
	echo "export FLASHIMAGE_KERNEL_OFFSET=${value}" >> ${FLASH_ENV_FILE}

	value="${@d.getVar('FLASHIMAGE_DTB_OFFSET', True)}"
	echo "export FLASHIMAGE_DTB_OFFSET=${value}" >> ${FLASH_ENV_FILE}

	value="${@d.getVar('FLASHIMAGE_ROOTFS_OFFSET', True)}"
	echo "export FLASHIMAGE_ROOTFS_OFFSET=${value}" >> ${FLASH_ENV_FILE}
}

do_compile_append_s32g() {
	value="${@d.getVar('FLASHIMAGE_PFE_OFFSET', True)}"
	echo "export FLASHIMAGE_PFE_OFFSET=${value}" >> ${FLASH_ENV_FILE}
}

do_install() {
	install -d ${D}/${sysconfdir}/profile.d/
	install -d ${D}/opt/
	install -m 0644 ${FLASH_ENV_FILE} ${D}/${sysconfdir}/profile.d/
	install -m 0644 ${FLASH_SCRIPT_FILE} ${D}/opt/

	chmod +x ${D}/opt/${FLASH_SCRIPT_FILE}
	rm ${FLASH_ENV_FILE}
}

FILES_${PN} += "${sysconfdir}/profile.d/qspi_env.sh /opt/${FLASH_SCRIPT_FILE}"
