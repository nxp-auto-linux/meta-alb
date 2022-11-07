# Copyright 2019-2022 NXP
#
# This is the PFE Slave driver for Linux kernel 5.4 and 5.10
# This driver can be used only in PFE Master/Slave configuration.
# PFE Master driver is required to run the Slave driver.

require pfe_common.inc

EXTRA_OEMAKE:append = " PFE_CFG_MULTI_INSTANCE_SUPPORT=1 PFE_CFG_PFE_MASTER=0"

module_do_install() {
	install -D "${MDIR}/pfeng-slave.ko" "${INSTALL_DIR}/pfeng-slave.ko"
}

FILES:${PN} += "${sysconfdir}/modules-load.d/*"
