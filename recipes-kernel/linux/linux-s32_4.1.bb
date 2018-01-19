require recipes-kernel/linux/linux-s32.inc

SRC_URI = "git://source.codeaurora.org/external/autobsps32/linux;protocol=https;branch=alb/master"

# BSP15.0
SRCREV = "2433cb4d0d533a6556f229b9974ef96063884394"

DELTA_KERNEL_DEFCONFIG_append_s32v234bbmini += " \
    ${THISDIR}/linux-s32/build/blueboxconfig_s32v234pcie \
"

DELTA_KERNEL_DEFCONFIG_append_s32v234bbmini += "${THISDIR}/linux-s32/build/vnet_s32.cfg"

# LXC configuration
DELTA_KERNEL_DEFCONFIG_append = "${@bb.utils.contains('DISTRO_FEATURES', 'lxc', '${THISDIR}/linux-s32/build/containers_4.1.26.config', '', d)}"

# add sources for virtual ethernet over PCIe
SRC_URI_append_s32v234bbmini += "\
    git://source.codeaurora.org/external/autobsps32/vnet;protocol=https;branch=alb/master;name=vnet;destsuffix=git/drivers/net/vnet \
    file://0001-vnet-Add-initial-support-to-build-driver-in-kernel.patch \
"
SRCREV_vnet = "0a4dfebd961466f43e45047078a0c0e433a4470c"
