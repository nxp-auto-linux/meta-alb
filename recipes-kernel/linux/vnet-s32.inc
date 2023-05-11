# virtual ethernet support for linux kernel

DELTA_KERNEL_DEFCONFIG_append_s32v234bbmini += "vnet_s32.cfg"

# add sources for virtual ethernet over PCIe
SRC_URI_append_s32v234bbmini += "\
    git://github.com/nxp-auto-linux/vnet;protocol=https;branch=alb/master;name=vnet;destsuffix=git/drivers/net/vnet \
    file://0001-vnet-Add-initial-support-to-build-driver-in-kernel-${PV}.patch \
    file://build/vnet_s32.cfg \
"
SRCREV_vnet_s32v234bbmini = "a62c8e029dabaf7daeeb6e7ed9c6362bb48cf35b"
