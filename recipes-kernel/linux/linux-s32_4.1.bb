require recipes-kernel/linux/linux-s32.inc

SRC_URI = "git://source.codeaurora.org/external/autobsps32/linux;protocol=https;branch=alb/master"

SRCREV = "0df2715d17616d5db4dacf867fb20a81f328cd50"

DELTA_KERNEL_DEFCONFIG_append_s32v234pcie += " \
    blueboxconfig_s32v234pcie \
"
DELTA_KERNEL_DEFCONFIG_append_s32v234pciebcm += " \
    blueboxconfig_s32v234pcie \
"
DELTA_KERNEL_DEFCONFIG_append_s32v234bbmini += " \
    blueboxconfig_s32v234pcie \
"

DELTA_KERNEL_DEFCONFIG_append_s32v234bbmini += "vnet_s32.cfg"

# LXC configuration
DELTA_KERNEL_DEFCONFIG_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'lxc', 'containers_4.1.26.config', '', d)}"

# Docker configuration
DELTA_KERNEL_DEFCONFIG_append += "${@bb.utils.contains('DISTRO_FEATURES', 'docker', 'docker.config', '', d)}"

SRC_URI += "\
    file://build/blueboxconfig_s32v234pcie \
    file://build/vnet_s32.cfg \
    file://build/containers_4.1.26.config \
    file://build/docker.config \
"
# add sources for virtual ethernet over PCIe
SRC_URI_append_s32v234bbmini += "\
    git://source.codeaurora.org/external/autobsps32/vnet;protocol=https;branch=alb/master;name=vnet;destsuffix=git/drivers/net/vnet \
    file://0001-vnet-Add-initial-support-to-build-driver-in-kernel.patch \
"
SRCREV_vnet = "7d3c52dc3c6564e92bbbc0d0b6aa11e73174fed0"
