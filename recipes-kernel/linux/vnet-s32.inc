# virtual ethernet support for linux kernel

DELTA_KERNEL_DEFCONFIG_append_s32v234bbmini += "vnet_s32.cfg"

# add sources for virtual ethernet over PCIe
SRC_URI_append_s32v234bbmini += "\
    git://source.codeaurora.org/external/autobsps32/vnet;protocol=https;branch=alb/master;name=vnet;destsuffix=git/drivers/net/vnet \
    file://0001-vnet-Add-initial-support-to-build-driver-in-kernel.patch \
    file://build/vnet_s32.cfg \
"
SRCREV_vnet_s32v234bbmini = "213d3b4c6e9150885a44af3b884b90e2ccb3bcd5"
