require recipes-kernel/linux/linux-s32.inc

SRC_URI = "git://source.codeaurora.org/external/autobsps32/linux;protocol=https;branch=alb-4.14"
SRCREV = "750c5aa87e0b2a9e1b53fee151dd05127275087d"

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

# Add CCI device tree node for S32Gen1 only if GMAC is enabled
SRC_URI_append_gen1 = "${@bb.utils.contains('DISTRO_FEATURES', 'gmac', \
    ' file://0001-s32gen1-Add-CCI400-device-tree-configuration.patch ', \
    '', d)}"

# Following patch disables the AVB TX queues (1 and 2) in order to prevent
# a FEC TX queue timeout that occurs when using NFS root filesystem.
# This issue occurs on S32V234.
KERNEL_FEC_LIMIT_TX ??= "0"

require vnet-s32.inc

SRC_URI_append_s32v2xx = " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', ' file://0001-pcie-s32v-kernel-support-for-pcie-demos-icc-and-user-sp.patch', '', d)}"
SRC_URI_append_s32v2xx = " ${@oe.utils.conditional('KERNEL_FEC_LIMIT_TX', '0', '', ' file://0001-fec-limit-TX-queues-to-prevent-TX-starvation-crash.patch', d)}"

# Enable PCIe support for the EVBs and SBC also
DELTA_KERNEL_DEFCONFIG_append_s32v234evb += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', 'blueboxconfig_s32v234pcie_${PV}', '', d)}"
DELTA_KERNEL_DEFCONFIG_append_s32v234evb28899 += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', 'blueboxconfig_s32v234pcie_${PV}', '', d)}"
DELTA_KERNEL_DEFCONFIG_append_s32v234sbc += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', 'blueboxconfig_s32v234pcie_${PV}', '', d)}"

# Enable Xen booting
require ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'recipes-kernel/linux/linux-yocto_virtualization.inc', '', d)}
DELTA_KERNEL_DEFCONFIG_append += "${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'xen.cfg', '', d)}"
SRC_URI += "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'file://xen.cfg', '', d)} "
