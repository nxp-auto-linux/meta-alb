require recipes-kernel/linux/linux-s32.inc

SRC_URI = "git://source.codeaurora.org/external/autobsps32/linux;protocol=https;branch=alb/master"
SRCREV = "5b7d97940095b32ad96c522101befae48c6773c0"

DELTA_KERNEL_DEFCONFIG_append_s32v234pcie += " \
    blueboxconfig_s32v234pcie_${PV} \
"
DELTA_KERNEL_DEFCONFIG_append_s32v234pciebcm += " \
    blueboxconfig_s32v234pcie_${PV} \
"
DELTA_KERNEL_DEFCONFIG_append_s32v234bbmini += " \
    blueboxconfig_s32v234pcie_${PV} \
"

# LXC configuration
DELTA_KERNEL_DEFCONFIG_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'lxc', ' containers_${PV}.config', '', d)}"

# VIRTIO
DELTA_KERNEL_DEFCONFIG_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'virtio', 'virtio', '', d)}"

# Docker configuration
DELTA_KERNEL_DEFCONFIG_append += "${@bb.utils.contains('DISTRO_FEATURES', 'docker', 'docker.config', '', d)}"

# GPU configuration
DELTA_KERNEL_DEFCONFIG_append += "${@bb.utils.contains('DISTRO_FEATURES', 'gpu', 'gpu.config', '', d)}"

# Temperature Monitoring Unit
DELTA_KERNEL_DEFCONFIG_append_s32v234bbmini += "tmu.config"
DELTA_KERNEL_DEFCONFIG_append_s32v234evb += "tmu.config"

SRC_URI += "\
    file://build/blueboxconfig_s32v234pcie_${PV} \
    file://build/containers_${PV}.config \
    file://build/docker.config \
    file://build/virtio \
    file://build/gpu.config \
    file://build/tmu.config \
"

require vnet-s32.inc

SRC_URI_append_s32v234bbmini += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', 'file://0001-pcie-s32v-kernel-support-for-pcie-demos-icc-and-user-sp.patch', '', d)}"

# Enable PCIe support for the EVBs also
DELTA_KERNEL_DEFCONFIG_append_s32v234evb += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', 'blueboxconfig_s32v234pcie_${PV}', '', d)}"
DELTA_KERNEL_DEFCONFIG_append_s32v234evb28899 += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', 'blueboxconfig_s32v234pcie_${PV}', '', d)}"
