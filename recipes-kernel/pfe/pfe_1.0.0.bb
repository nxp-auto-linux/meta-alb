# Copyright 2019-2022 NXP
#
# This is the PFE driver for Linux kernel 5.4 and 5.10
# This driver can be used in PFE Master/Slave configuration as Master.

inherit deploy

require pfe_common.inc

PFE_FW_CLASS_BIN ?= "s32g_pfe_class.fw"
PFE_FW_UTIL_BIN ?= "s32g_pfe_util.fw"
FW_INSTALL_CLASS_NAME ?= "s32g_pfe_class.fw"
FW_INSTALL_UTIL_NAME ?= "s32g_pfe_util.fw"

SRC_URI:append = " \
	file://${NXP_FIRMWARE_LOCAL_DIR}/${PFE_FW_CLASS_BIN} \
	file://${NXP_FIRMWARE_LOCAL_DIR}/${PFE_FW_UTIL_BIN} \
"

# Dummy entry to keep the recipe parser happy if we don't use this recipe
NXP_FIRMWARE_LOCAL_DIR ?= "."

FW_INSTALL_DIR = "${D}/${base_libdir}/firmware"

# Tell yocto not to bother stripping our binaries, especially the firmware
INHIBIT_PACKAGE_STRIP_FILES = "\
    ${PKGD}${base_libdir}/firmware/s32g_pfe_class.fw \
    ${PKGD}${base_libdir}/firmware/s32g_pfe_util.fw \
"

# In case the pfe-slave is built, change to multi instance driver(allow override)
PFE_MASTER_OPTIONS ?= "${@bb.utils.contains('DISTRO_FEATURES', 'pfe-slave', ' PFE_CFG_MULTI_INSTANCE_SUPPORT=1 PFE_CFG_PFE_MASTER=1', '', d)}"
EXTRA_OEMAKE:append = " ${PFE_MASTER_OPTIONS}"

module_do_install() {
	install -D "${MDIR}/pfeng.ko" "${INSTALL_DIR}/pfeng.ko"

	install -d "${FW_INSTALL_DIR}"
	install -D "${WORKDIR}/${NXP_FIRMWARE_LOCAL_DIR}/${PFE_FW_CLASS_BIN}" "${FW_INSTALL_DIR}/${FW_INSTALL_CLASS_NAME}"
	install -D "${WORKDIR}/${NXP_FIRMWARE_LOCAL_DIR}/${PFE_FW_UTIL_BIN}" "${FW_INSTALL_DIR}/${FW_INSTALL_UTIL_NAME}"
}

# Deploy FW for u-boot
do_deploy() {
	install -d ${DEPLOYDIR}

	if [ -f ${FW_INSTALL_DIR}/${FW_INSTALL_CLASS_NAME} ];then
		install -m 0644 ${FW_INSTALL_DIR}/${FW_INSTALL_CLASS_NAME} ${DEPLOYDIR}/${FW_INSTALL_CLASS_NAME}
	fi
}

addtask do_deploy after do_install

FILES:${PN} += "${sysconfdir}/modules-load.d/*"
FILES:${PN} += "${base_libdir}/firmware"

# avoid "QA Issue: Architecture did not match" caused by firmware
INSANE_SKIP:${PN} += "arch already-stripped"
