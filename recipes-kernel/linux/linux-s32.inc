DESCRIPTION = "Linux kernel for S32 platforms"
SECTION = "kernel"
LICENSE = "GPL-2.0-only"

inherit kernel

inherit fsl-kernel-localversion

SCMVERSION ?= "y"
LOCALVERSION = ""
DELTA_KERNEL_DEFCONFIG ?= ""

URL ?= "git://github.com/nxp-auto-linux/linux;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}-rt"
SRC_URI = "${URL};branch=${BRANCH}"

DEPENDS:append = " libgcc dtc-native"
DEPENDS:append = "${@ ' u-boot-tools-scmi-native' if d.getVar('SCMI_DTB_NODE_CHANGE') else ''}"
DEPENDS:append = " arm-trusted-firmware-scmi-hdrs"

KERNEL_CC:append = " ${TOOLCHAIN_OPTIONS}"
KERNEL_LD:append = " ${TOOLCHAIN_OPTIONS}"

PINCTRL_OPT = "${@oe.utils.conditional('SCMI_USE_SCMI_PINCTRL', '1', '--pinctrl', '--no-pinctrl', d)}"
GPIO_OPT = "${@oe.utils.conditional('SCMI_USE_SCMI_GPIO', '1', '--gpio', '--no-gpio', d)}"
NVMEM_OPT = "${@oe.utils.conditional('SCMI_USE_SCMI_NVMEM', '1', '--nvmem', '--no-nvmem', d)}"

S = "${WORKDIR}/git"

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
addtask merge_delta_config before do_kernel_localversion after do_patch

# Fix the dtc compile issue if DTC related options are not enabled in defconfig
do_compile:prepend() {
    mkdir -p ${B}/scripts/dtc
    if [ ! -e ${B}/scripts/dtc/dtc ]; then
        ln -sf ${STAGING_BINDIR_NATIVE}/dtc ${B}/scripts/dtc/dtc
    fi

    for hdr in ${STAGING_INCDIR}/scmi-hdrs/*; do
        bname="$(basename ${hdr})"
        lhdr=$(find "${S}/include" -name "${bname}")

        if [ -z "${lhdr}" ]; then
            bbfatal_log "Failed to locate ${bname} header file"
        fi

        if ! diff -NZbau "${hdr}" "${lhdr}"; then
            bbfatal_log "There is a difference in the SCMI header content between TF-A and the Linux repository (${hdr} vs ${lhdr})."
        fi
    done
}

# Switch from SIUL2 to SCMI drivers if needed
do_compile:append() {
	dtb_name="${B}/arch/arm64/boot/dts/${KERNEL_DEVICETREE}"
	if ${SCMI_DTB_NODE_CHANGE}; then
		nativepython3 ${STAGING_BINDIR_NATIVE}/scmi_dtb_node_change.py \
			${dtb_name} \
			${GPIO_OPT} \
			${PINCTRL_OPT} \
            ${NVMEM_OPT}
	fi
}

do_install:append:s32() {
    if [ -f "arch/${ARCH}/boot/zImage" ]; then
        install -m 0644 arch/${ARCH}/boot/zImage ${D}/boot/zImage-${KERNEL_VERSION}
        ln -sf  zImage-${KERNEL_VERSION} ${D}/boot/zImage
    fi
}

do_deploy:append:s32() {
    if [ -f "arch/${ARCH}/boot/zImage" ]; then
        install -m 0644 arch/${ARCH}/boot/zImage ${DEPLOYDIR}/${ZIMAGE_BASE_NAME}.bin
        ln -sf ${ZIMAGE_BASE_NAME}.bin ${DEPLOYDIR}/zImage-${MACHINE}.bin
        ln -sf ${ZIMAGE_BASE_NAME}.bin ${DEPLOYDIR}/zImage
    fi
}

# run make scripts after the shared work folder is created to prepare further
# module build. The module's make scripts may have been already executed
# before linux clean, and thus will not re-trigger again
do_shared_workdir:append () {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	make CC="${KERNEL_CC}" LD="${KERNEL_LD}" AR="${KERNEL_AR}" \
	           -C ${STAGING_KERNEL_DIR} O=${STAGING_KERNEL_BUILDDIR} scripts
}

FILES:${KERNEL_PACKAGE_NAME}-image += "/boot/zImage*"

KERNEL_MODULE_AUTOLOAD:s32g274aevb += "\
    nxp \
"

# LXC configuration
DELTA_KERNEL_DEFCONFIG:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'lxc', ' containers.config', '', d)}"

# VIRTIO
DELTA_KERNEL_DEFCONFIG:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'virtio', ' virtio', '', d)}"

# Docker configuration
DELTA_KERNEL_DEFCONFIG:append = "${@bb.utils.contains('DISTRO_FEATURES', 'docker', ' docker.config', '', d)}"
SRC_URI += "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'docker', 'file://build/docker.config', '', d)} "

# GPU configuration
DELTA_KERNEL_DEFCONFIG:append = "${@bb.utils.contains('DISTRO_FEATURES', 'gpu', ' gpu.config', '', d)}"

# MMC test configuration
DELTA_KERNEL_DEFCONFIG:append = "${@bb.utils.contains('DISTRO_FEATURES', 'bsp-utils', ' mmc_test.cfg', '', d)}"


SRC_URI += "\
    file://build/containers.config \
    file://build/virtio \
    file://build/gpu.config \
    file://build/mmc_test.cfg \
"

require vnet-s32.inc
require llce.inc

# Since Kernel version 4.19, task 'do_merge_delta_config' requires that the cross
# compiler is available in recipe's sysroot. In order to avoid any erros/warnings
# at build time of the Linux Kernel version 5.4, we add this dependency.
do_merge_delta_config[depends] += "virtual/${TARGET_PREFIX}gcc:do_populate_sysroot"

DEPENDS = "flex-native bison-native bc-native"

# Enable Xen booting
require ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'recipes-kernel/linux/linux-yocto_virtualization.inc', '', d)}
DELTA_KERNEL_DEFCONFIG:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'xen.cfg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'xen_s32.cfg', '', d)} \
    "
SRC_URI += "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'file://xen.cfg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'file://build/xen_s32.cfg', '', d)} \
    "

# Enable Trusted Execution Environment (TEE) support and add the OP-TEE driver
DELTA_KERNEL_DEFCONFIG:append:s32cc = "${@bb.utils.contains('DISTRO_FEATURES', 'optee', ' optee.cfg', '', d)}"
SRC_URI:append:s32cc = " ${@bb.utils.contains('DISTRO_FEATURES', 'optee', ' file://build/optee.cfg', '', d)}"

# make everything compatible for the time being
COMPATIBLE_MACHINE:$MACHINE = "$MACHINE"

# Enable dm-verity
DELTA_KERNEL_DEFCONFIG:append:s32cc = "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', ' dm-verity.cfg', '', d)}"
SRC_URI:append:s32cc = "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', ' file://build/dm-verity.cfg', '', d)}"

# Enable support for pcie demos
SRC_URI:append:s32cc = "${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos', ' file://build/pcie_demos.config', '', d)}"
DELTA_KERNEL_DEFCONFIG:append:s32cc = "${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos', ' pcie_demos.config', '', d)}"
