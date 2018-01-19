DESCRIPTION = "Linux kernel for S32V2xx platforms"
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM ?= "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel

inherit fsl-kernel-localversion

SCMVERSION ?= "y"
LOCALVERSION = ""
DELTA_KERNEL_DEFCONFIG ?= ""

DEPENDS_append = " libgcc dtc-native"

KERNEL_CC_append = " ${TOOLCHAIN_OPTIONS}"
KERNEL_LD_append = " ${TOOLCHAIN_OPTIONS}"

S = "${WORKDIR}/git"
do_preconfigure_prepend() {
    mkdir -p ${B}
    # copy desired defconfig so we pick it up for the real kernel_do_configure
    cp ${KERNEL_DEFCONFIG} ${B}/.config

    # During development we want to do easy config overrides
    # add config fragments
    for deltacfg in ${DELTA_KERNEL_DEFCONFIG}; do
        if [ -f "${deltacfg}" ]; then
            ${S}/scripts/kconfig/merge_config.sh -m ${B}/.config ${deltacfg}
        elif [ -f "${WORKDIR}/${deltacfg}" ]; then
            ${S}/scripts/kconfig/merge_config.sh -m ${B}/.config ${WORKDIR}/${deltacfg}
        elif [ -f "${S}/arch/${ARCH}/configs/${deltacfg}" ]; then
            ${S}/scripts/kconfig/merge_config.sh -m ${B}/.config \
                ${S}/arch/${ARCH}/configs/${deltacfg}
        fi
    done
    # copy .config to defconfig as required by fsl-kernel-localversion
    cp ${B}/.config ${WORKDIR}/defconfig
}

# Fix the dtc compile issue if DTC related options are not enabled in defconfig
do_compile_prepend() {
    mkdir -p ${B}/scripts/dtc
    if [ ! -e ${B}/scripts/dtc/dtc ]; then
        ln -sf ${STAGING_BINDIR_NATIVE}/dtc ${B}/scripts/dtc/dtc
    fi
}

do_install_append_s32v() {
    if [ -f "arch/${ARCH}/boot/zImage" ]; then
        install -m 0644 arch/${ARCH}/boot/zImage ${D}/boot/zImage-${KERNEL_VERSION}
        ln -sf  zImage-${KERNEL_VERSION} ${D}/boot/zImage
    fi
}

do_deploy_append_s32v() {
    if [ -f "arch/${ARCH}/boot/zImage" ]; then
        install -m 0644 arch/${ARCH}/boot/zImage ${DEPLOYDIR}/${ZIMAGE_BASE_NAME}.bin
        ln -sf ${ZIMAGE_BASE_NAME}.bin ${DEPLOYDIR}/zImage-${MACHINE}.bin
        ln -sf ${ZIMAGE_BASE_NAME}.bin ${DEPLOYDIR}/zImage
    fi
}

FILES_kernel-image += "/boot/zImage*"

# make everything compatible for the time being
COMPATIBLE_MACHINE_$MACHINE = "$MACHINE"
