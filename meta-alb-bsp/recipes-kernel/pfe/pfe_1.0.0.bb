# Copyright 2019-2022,2025 NXP
#
# This is the PFE driver for Linux kernel 5.4 and 5.10
# This driver can be used in PFE Master/Slave configuration as Master.

require pfe_common.inc

# In case the pfe-slave is built, change to multi instance driver(allow override)
PFE_MASTER_OPTIONS ?= "${@bb.utils.contains('DISTRO_FEATURES', 'pfe-slave', ' PFE_CFG_MULTI_INSTANCE_SUPPORT=1 PFE_CFG_PFE_MASTER=1', '', d)}"
EXTRA_OEMAKE:append = " ${PFE_MASTER_OPTIONS}"
RDEPENDS:${PN} += "pfe-firmware"

module_do_install() {
	install -D "${MDIR}/pfeng.ko" "${INSTALL_DIR}/pfeng.ko"
}

FILES:${PN} += "${sysconfdir}/modules-load.d/*"
