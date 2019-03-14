# Copyright 2018 NXP
#
# This is the GMAC driver for Linux kernel 4.14

SUMMARY = "Linux driver for transmitting and receiving Ethernet frames using the GMAC hardware"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://net/ethernet/stmicro/stmmac/Makefile;endline=1;md5=daad6f7f7a0a286391cd7773ccf79340"

inherit module

SRC_URI = " \
	git://source.codeaurora.org/external/autobsps32/extra/gmac;protocol=https \
	file://Makefile \
	"
SRCREV = "7f4dcf44303aeb5a1fca849054d3fde1dee7e534"

S = "${WORKDIR}/git/drivers"
MDIR = "${S}/net/ethernet/stmicro/stmmac"
INSTALL_DIR = "${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/net/ethernet/stmicro/stmmac"

EXTRA_OEMAKE_append = " KDIR=${STAGING_KERNEL_DIR} MDIR=${MDIR} -C ${WORKDIR} "

module_do_install() {
	install -D ${MDIR}/stmmac-platform.ko ${INSTALL_DIR}/stmmac-platform.ko
	install -D ${MDIR}/dwmac-s32cc.ko ${INSTALL_DIR}/dwmac-s32cc.ko
	install -D ${MDIR}/stmmac.ko ${INSTALL_DIR}/stmmac.ko
}

# We may want to autoload it in the future, after the SMP issue is solved
#KERNEL_MODULE_AUTOLOAD += "stmmac.ko stmmac-platform.ko dwmac_s32cc"

FILES_${PN} += "${base_libdir}/*"
FILES_${PN} += "${sysconfdir}/modules-load.d/*"

PROVIDES = " \
	kernel-module-stmmac${KERNEL_MODULE_PACKAGE_SUFFIX} \
	kernel-module-stmmac-platform${KERNEL_MODULE_PACKAGE_SUFFIX} \
	kernel-module-dwmac-s32cc${KERNEL_MODULE_PACKAGE_SUFFIX} \
	"
RPROVIDES_${PN} = " \
	kernel-module-stmmac${KERNEL_MODULE_PACKAGE_SUFFIX} \
	kernel-module-stmmac-platform${KERNEL_MODULE_PACKAGE_SUFFIX} \
	kernel-module-dwmac-s32cc${KERNEL_MODULE_PACKAGE_SUFFIX} \
	"

COMPATIBLE_MACHINE = "gen1"
