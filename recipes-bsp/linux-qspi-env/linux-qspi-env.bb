# Copyright 2022 NXP

SUMMARY = "QSPI Flash Linux environment offsets deployment"
LICENSE = "BSD"

S = "${WORKDIR}"
DEPLOY_FILE = "${S}/qspi_env.sh"

do_compile() {
	value="${@d.getVar('FLASHIMAGE_FIP_OFFSET', True)}"
	echo "export FLASHIMAGE_FIP_OFFSET=${value}" > ${DEPLOY_FILE}

	value="${@d.getVar('FLASHIMAGE_UBOOT_ENV_OFFSET', True)}"
	echo "export FLASHIMAGE_UBOOT_ENV_OFFSET=${value}" >> ${DEPLOY_FILE}

	value="${@d.getVar('FLASHIMAGE_KERNEL_OFFSET', True)}"
	echo "export FLASHIMAGE_KERNEL_OFFSET=${value}" >> ${DEPLOY_FILE}

	value="${@d.getVar('FLASHIMAGE_DTB_OFFSET', True)}"
	echo "export FLASHIMAGE_DTB_OFFSET=${value}" >> ${DEPLOY_FILE}

	value="${@d.getVar('FLASHIMAGE_ROOTFS_OFFSET', True)}"
	echo "export FLASHIMAGE_ROOTFS_OFFSET=${value}" >> ${DEPLOY_FILE}
}

do_compile_append_s32g() {
	value="${@d.getVar('FLASHIMAGE_PFE_OFFSET', True)}"
	echo "export FLASHIMAGE_PFE_OFFSET=${value}" >> ${FLASH_ENV_FILE}
}

do_install() {
	install -d ${D}/${sysconfdir}/profile.d/
	install -m 0644 ${DEPLOY_FILE} ${D}/${sysconfdir}/profile.d/

	rm ${DEPLOY_FILE}
}

FILES_${PN} += "${sysconfdir}/profile.d/qspi_env.sh"
