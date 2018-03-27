require recipes-kernel/linux/linux-s32.inc

SRC_URI = "git://source.codeaurora.org/external/autobsps32/linux;protocol=https;branch=alb/master"

# BSP16.1
SRCREV = "bda2b350d5cba228fdc2614e54f77e1f43229346"

DELTA_KERNEL_DEFCONFIG_append_s32v234bbmini += " \
    ${THISDIR}/linux-s32/build/blueboxconfig_s32v234pcie \
"

DELTA_KERNEL_DEFCONFIG_append_s32v234bbmini += "${THISDIR}/linux-s32/build/vnet_s32.cfg"

# LXC configuration
DELTA_KERNEL_DEFCONFIG_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'lxc', '${THISDIR}/linux-s32/build/containers_4.1.26.config', '', d)}"

# Docker configuration
DELTA_KERNEL_DEFCONFIG_append += "${@bb.utils.contains('DISTRO_FEATURES', 'docker', '${THISDIR}/linux-s32/build/docker.config', '', d)}"

# add sources for virtual ethernet over PCIe
SRC_URI_append_s32v234bbmini += "\
    git://source.codeaurora.org/external/autobsps32/vnet;protocol=https;branch=alb/master;name=vnet;destsuffix=git/drivers/net/vnet \
    file://0001-vnet-Add-initial-support-to-build-driver-in-kernel.patch \
"
SRCREV_vnet = "a7d9c2ccb0c6bb03d652dce5721f8aa416220ac7"
