require recipes-kernel/linux/linux-s32.inc

URL ?= "git://source.codeaurora.org/external/autobsps32/linux;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}-rt"
SRC_URI = "${URL};branch=${BRANCH}"
SRCREV = "056e0ae0f5a772991b79310262904a5884f43bf8"

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

DELTA_KERNEL_DEFCONFIG_append_s32v234pcie += " \
    blueboxconfig_s32v234pcie_${PV}.cfg \
"
DELTA_KERNEL_DEFCONFIG_append_s32v234pciebcm += " \
    blueboxconfig_s32v234pcie_${PV}.cfg \
"
DELTA_KERNEL_DEFCONFIG_append_s32v234bbmini += " \
    blueboxconfig_s32v234pcie_${PV}.cfg \
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
    file://build/blueboxconfig_s32v234pcie_${PV}.cfg \
    file://build/containers_${PV}.config \
    file://build/docker.config \
    file://build/virtio \
    file://build/gpu.config \
    file://build/tmu.config \
"

# Following patch disables the AVB TX queues (1 and 2) in order to prevent
# a FEC TX queue timeout that occurs when using NFS root filesystem.
# This issue occurs on S32V234.
KERNEL_FEC_LIMIT_TX ??= "0"

require vnet-s32.inc
require gcc75compat.inc

# For Kernel version 5.4, task 'do_merge_delta_config' requires that the cross
# compiler is available in recipe's sysroot. In order to avoid any erros/warnings
# at build time of the Linux Kernel version 5.4, we add this dependency.
do_merge_delta_config[depends] += "virtual/${TARGET_PREFIX}gcc:do_populate_sysroot"

# TODO: pcie-demos not ported to 5.4
SRC_URI_append_s32v2xx = " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', ' file://0001-pcie-s32v-kernel-support-for-pcie-demos-icc-and-user-sp.patch', '', d)}"

SRC_URI_append_s32v2xx = " ${@oe.utils.conditional('KERNEL_FEC_LIMIT_TX', '0', '', ' file://0001-fec-limit-TX-queues-to-prevent-TX-starvation-crash.patch', d)}"

# Enable PCIe support for the EVBs and SBC also
DELTA_KERNEL_DEFCONFIG_append_s32v234evb += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', 'blueboxconfig_s32v234pcie_${PV}.cfg', '', d)}"
DELTA_KERNEL_DEFCONFIG_append_s32v234evb28899 += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', 'blueboxconfig_s32v234pcie_${PV}.cfg', '', d)}"
DELTA_KERNEL_DEFCONFIG_append_s32v234sbc += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', 'blueboxconfig_s32v234pcie_${PV}.cfg', '', d)}"

DEPENDS = "flex-native bison-native bc-native"

# Enable Xen booting
require ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'recipes-kernel/linux/linux-yocto_virtualization.inc', '', d)}
DELTA_KERNEL_DEFCONFIG_append_gen1 += "${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'xen_watchdog.cfg', '', d)}"
DELTA_KERNEL_DEFCONFIG_append += "${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'xen.cfg', '', d)}"
SRC_URI_append_gen1 = "${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'file://build/xen_watchdog.cfg', '', d)} "
SRC_URI += "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'file://xen.cfg', '', d)} "

# Add pcie for S32G and S32R
SRC_URI_append_gen1 = " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie', \
    'file://build/pcie_s32gen1.cfg', '', d)}"
DELTA_KERNEL_DEFCONFIG_append_gen1 += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie', 'pcie_s32gen1.cfg', '', d)}"

# Enable 100MB BAR support for S32G and R
SRC_URI_append_gen1 += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-large-bars', \
	'0001-s32gen1-Increase-reserved-mem-and-EP-BAR-2-to-100MB.patch', '', d)}"

# Enable Trusted Execution Environment (TEE) support and add the OP-TEE driver
DELTA_KERNEL_DEFCONFIG_append_s32g274aevb += "${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'optee.cfg', '', d)}"
SRC_URI_append_s32g274aevb = " ${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'file://build/optee.cfg', '', d)}"

DELTA_KERNEL_DEFCONFIG_append_s32g274ardb += "${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'optee.cfg', '', d)}"
SRC_URI_append_s32g274ardb = " ${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'file://build/optee.cfg', '', d)}"
