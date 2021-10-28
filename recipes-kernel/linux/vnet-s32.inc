# virtual ethernet support for linux kernel

DELTA_KERNEL_DEFCONFIG_append_s32v234bbmini += "vnet_s32.cfg"

# add sources for virtual ethernet over PCIe
VNET_URL ?= "git://source.codeaurora.org/external/autobsps32/vnet;protocol=https"
VNET_BRANCH ?= "${RELEASE_BASE}"

MIN_VER =  "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI_append_s32v234bbmini += "\
    ${VNET_URL};branch=${VNET_BRANCH};name=vnet;destsuffix=git/drivers/net/vnet \
    file://0001-vnet-Add-initial-support-to-build-driver-in-kernel-${MIN_VER}.patch \
    file://build/vnet_s32.cfg \
"
SRCREV_vnet_s32v234bbmini = "c67b84a6194c6ec47fde15bb7d5022eca2b1f894"
