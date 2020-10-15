# virtual ethernet support for linux kernel

DELTA_KERNEL_DEFCONFIG_append_s32v234bbmini += "vnet_s32.cfg"

# add sources for virtual ethernet over PCIe
VNET_URL ?= "git://source.codeaurora.org/external/autobsps32/vnet;protocol=https"
VNET_BRANCH ?= "${RELEASE_BASE}"

SRC_URI_append_s32v234bbmini += "\
    ${VNET_URL};branch=${VNET_BRANCH};name=vnet;destsuffix=git/drivers/net/vnet \
    file://0001-vnet-Add-initial-support-to-build-driver-in-kernel-${PV}.patch \
    file://build/vnet_s32.cfg \
"
SRCREV_vnet_s32v234bbmini = "841ba3369b060afc99d61afe559251906da4fcb0"
