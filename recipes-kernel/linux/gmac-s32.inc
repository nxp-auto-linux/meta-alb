# virtual ethernet support for linux kernel

DELTA_KERNEL_DEFCONFIG_append_gen1 += "gmac_s32.cfg"

# add sources for GMAC 0.4.2 
SRC_URI_append_gen1 += "\
    git://source.codeaurora.org/external/autobsps32/extra/gmac;protocol=https;name=gmac;destsuffix=git/gmac \
    file://build/gmac_s32.cfg \
    file://0001-gmac-integrate-dwmac-s32cc-module-in-the-kernel-build.patch \
    file://0002-gmac-Add-device-tree-node-for-GMAC-0.4.2.patch \
    file://0001-s32gen1-Add-CCI400-device-tree-configuration.patch \
"

do_preconfigure_prepend_gen1() {
	cp -f "${S}/gmac/drivers/net/ethernet/stmicro/stmmac/dwmac-s32cc.c" "${S}/drivers/net/ethernet/stmicro/stmmac/"
}

SRCREV_gmac_gen1 = "66d2d4e868cdcf6bb75b0c6d7e4c82bcf1476aab"
