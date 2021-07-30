DESCRIPTION = "Linux kernel for S32 platforms"
SECTION = "kernel"
LICENSE = "GPLv2"

inherit kernel

inherit fsl-kernel-localversion

SCMVERSION ?= "y"
LOCALVERSION = ""
DELTA_KERNEL_DEFCONFIG ?= ""

URL ?= "git://source.codeaurora.org/external/autobsps32/linux;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}-rt"
SRC_URI = "${URL};branch=${BRANCH}"

DEPENDS_append = " libgcc dtc-native"

KERNEL_CC_append = " ${TOOLCHAIN_OPTIONS}"
KERNEL_LD_append = " ${TOOLCHAIN_OPTIONS}"

S = "${WORKDIR}/git"
MAJ_VER =  "${@oe.utils.trim_version("${PV}", 1)}"

do_merge_delta_config[dirs] = "${B}"
do_merge_delta_config() {
#    # copy desired defconfig so we pick it up for the real kernel_do_configure
#    cp ${KERNEL_DEFCONFIG} ${B}/.config
    # create .config with make config
    oe_runmake  -C ${S} O=${B} ${KERNEL_DEFCONFIG}

    # add config fragments
    for deltacfg in ${DELTA_KERNEL_DEFCONFIG}; do
        if [ -f "${deltacfg}" ]; then
            ${S}/scripts/kconfig/merge_config.sh -m .config ${deltacfg}
        elif [ -f "${WORKDIR}/${deltacfg}" ]; then
            ${S}/scripts/kconfig/merge_config.sh -m .config ${WORKDIR}/${deltacfg}
        elif [ -f "${S}/arch/${ARCH}/configs/${deltacfg}" ]; then
            ${S}/scripts/kconfig/merge_config.sh -m .config \
                ${S}/arch/${ARCH}/configs/${deltacfg}
        fi
    done
    # copy .config to defconfig as required by fsl-kernel-localversion
    cp .config ${WORKDIR}/defconfig
}
addtask merge_delta_config before do_preconfigure after do_patch

# Fix the dtc compile issue if DTC related options are not enabled in defconfig
do_compile_prepend() {
    mkdir -p ${B}/scripts/dtc
    if [ ! -e ${B}/scripts/dtc/dtc ]; then
        ln -sf ${STAGING_BINDIR_NATIVE}/dtc ${B}/scripts/dtc/dtc
    fi
}

do_install_append_s32() {
    if [ -f "arch/${ARCH}/boot/zImage" ]; then
        install -m 0644 arch/${ARCH}/boot/zImage ${D}/boot/zImage-${KERNEL_VERSION}
        ln -sf  zImage-${KERNEL_VERSION} ${D}/boot/zImage
    fi
}

do_deploy_append_s32() {
    if [ -f "arch/${ARCH}/boot/zImage" ]; then
        install -m 0644 arch/${ARCH}/boot/zImage ${DEPLOYDIR}/${ZIMAGE_BASE_NAME}.bin
        ln -sf ${ZIMAGE_BASE_NAME}.bin ${DEPLOYDIR}/zImage-${MACHINE}.bin
        ln -sf ${ZIMAGE_BASE_NAME}.bin ${DEPLOYDIR}/zImage
    fi
}

# run make scripts after the shared work folder is created to prepare further
# module build. The module's make scripts may have been already executed
# before linux clean, and thus will not re-trigger again
do_shared_workdir_append () {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	make CC="${KERNEL_CC}" LD="${KERNEL_LD}" AR="${KERNEL_AR}" \
	           -C ${STAGING_KERNEL_DIR} O=${STAGING_KERNEL_BUILDDIR} scripts
}

FILES_${KERNEL_PACKAGE_NAME}-image += "/boot/zImage*"

# Support for TJA1100 and TJA1102 under Linux
KERNEL_MODULE_AUTOLOAD_s32v234bbmini += "\
    nxp \
"

KERNEL_MODULE_AUTOLOAD_s32v234evb += "\
    nxp \
"

KERNEL_MODULE_AUTOLOAD_s32g274aevb += "\
    nxp \
"

KERNEL_MODULE_AUTOLOAD_s32g274ardb += "\
    nxp \
"

DELTA_KERNEL_DEFCONFIG_append_s32v234pcie += " \
    blueboxconfig_s32v234pcie_${MAJ_VER}.x.cfg \
"
DELTA_KERNEL_DEFCONFIG_append_s32v234pciebcm += " \
    blueboxconfig_s32v234pcie_${MAJ_VER}.x.cfg \
"
DELTA_KERNEL_DEFCONFIG_append_s32v234bbmini += " \
    blueboxconfig_s32v234pcie_${MAJ_VER}.x.cfg \
"

# LXC configuration
DELTA_KERNEL_DEFCONFIG_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'lxc', ' containers.config', '', d)}"

# VIRTIO
DELTA_KERNEL_DEFCONFIG_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'virtio', 'virtio', '', d)}"

# Docker configuration
DELTA_KERNEL_DEFCONFIG_append += "${@bb.utils.contains('DISTRO_FEATURES', 'docker', 'docker.config', '', d)}"

# GPU configuration
DELTA_KERNEL_DEFCONFIG_append += "${@bb.utils.contains('DISTRO_FEATURES', 'gpu', 'gpu.config', '', d)}"

# Temperature Monitoring Unit
DELTA_KERNEL_DEFCONFIG_append_s32v234bbmini += "tmu.config"
DELTA_KERNEL_DEFCONFIG_append_s32v234evb += "tmu.config"

LLCE_FEATURES = "${@bb.utils.contains('DISTRO_FEATURES', 'llce-can', '+can', '', d)}"
LLCE_FEATURES_append = "${@bb.utils.contains('DISTRO_FEATURES', 'llce-logger', '+logger', '', d)}"

# LLCE CAN Drivers
DELTA_KERNEL_DEFCONFIG_append_s32g2 += "${@bb.utils.contains('DISTRO_FEATURES', 'llce-can', 'llce_can.config', '', d)}"
# LLCE logger
DELTA_KERNEL_DEFCONFIG_append_s32g2 += "${@bb.utils.contains('DISTRO_FEATURES', 'llce-logger', 'llce_logger.config', '', d)}"

# Logging only
KERNEL_MODULE_AUTOLOAD_append_s32g2 += "${@bb.utils.contains('LLCE_FEATURES', '+logger', 'llce-core llce-mailbox llce_logger', '', d)}"
KERNEL_MODULE_PROBECONF += "${@bb.utils.contains('LLCE_FEATURES', '+logger', 'llce-core llce-mailbox', '', d)}"
module_conf_llce-core = "${@bb.utils.contains('LLCE_FEATURES', '+logger', 'options llce-core load_fw=0', '', d)}"
module_conf_llce-mailbox = "${@bb.utils.contains('LLCE_FEATURES', '+logger', 'options llce-mailbox config_platform=0', '', d)}"

# CAN + Logging
KERNEL_MODULE_AUTOLOAD_append_s32g2 += "${@bb.utils.contains('LLCE_FEATURES', '+can+logger', 'llce-core llce-mailbox llce_can llce_logger', '', d)}"
KERNEL_MODULE_PROBECONF += "${@bb.utils.contains('LLCE_FEATURES', '+can+logger', 'llce_can', '', d)}"
module_conf_llce_can = "${@bb.utils.contains('LLCE_FEATURES', '+can+logger', 'options llce_can logging=1', '', d)}"

SRC_URI += "\
    file://build/blueboxconfig_s32v234pcie_${MAJ_VER}.x.cfg \
    file://build/containers.config \
    file://build/docker.config \
    file://build/virtio \
    file://build/gpu.config \
    file://build/tmu.config \
    file://build/llce_can.config \
    file://build/llce_logger.config \
"

# Following patch disables the AVB TX queues (1 and 2) in order to prevent
# a FEC TX queue timeout that occurs when using NFS root filesystem.
# This issue occurs on S32V234.
KERNEL_FEC_LIMIT_TX ??= "0"

require vnet-s32.inc
require gcc75compat.inc

# Since Kernel version 4.19, task 'do_merge_delta_config' requires that the cross
# compiler is available in recipe's sysroot. In order to avoid any erros/warnings
# at build time of the Linux Kernel version 5.4, we add this dependency.
do_merge_delta_config[depends] += "virtual/${TARGET_PREFIX}gcc:do_populate_sysroot"

# TODO: pcie-demos not ported to 5.4
SRC_URI_append_s32v2xx = " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', ' file://0001-pcie-s32v-kernel-support-for-pcie-demos-icc-and-user-sp.patch', '', d)}"

SRC_URI_append_s32v2xx = " ${@oe.utils.conditional('KERNEL_FEC_LIMIT_TX', '0', '', ' file://0001-fec-limit-TX-queues-to-prevent-TX-starvation-crash.patch', d)}"

# Enable PCIe support for the EVBs and SBC also
DELTA_KERNEL_DEFCONFIG_append_s32v234evb += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', 'blueboxconfig_s32v234pcie_${MAJ_VER}.x.cfg', '', d)}"
DELTA_KERNEL_DEFCONFIG_append_s32v234evb28899 += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', 'blueboxconfig_s32v234pcie_${MAJ_VER}.x.cfg', '', d)}"
DELTA_KERNEL_DEFCONFIG_append_s32v234sbc += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', 'blueboxconfig_s32v234pcie_${MAJ_VER}.x.cfg', '', d)}"

DEPENDS = "flex-native bison-native bc-native"

# Enable Xen booting
require ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'recipes-kernel/linux/linux-yocto_virtualization.inc', '', d)}
DELTA_KERNEL_DEFCONFIG_append += "${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'xen.cfg', '', d)}"
SRC_URI += "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'file://xen.cfg', '', d)} "

# Add pcie for S32G and S32R
SRC_URI_append_gen1 = " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie', \
    'file://build/pcie_s32gen1.cfg', '', d)}"
DELTA_KERNEL_DEFCONFIG_append_gen1 += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie', 'pcie_s32gen1.cfg', '', d)}"

# Enable Trusted Execution Environment (TEE) support and add the OP-TEE driver
DELTA_KERNEL_DEFCONFIG_append_s32g2 += "${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'optee.cfg', '', d)}"
SRC_URI_append_s32g2 = " ${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'file://build/optee.cfg', '', d)}"

# make everything compatible for the time being
COMPATIBLE_MACHINE_$MACHINE = "$MACHINE"

# Enable dm-verity
DELTA_KERNEL_DEFCONFIG_append_gen1 += "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'dm-verity.cfg', '', d)}"
SRC_URI_append_gen1 += "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'file://build/dm-verity.cfg', '', d)}"
