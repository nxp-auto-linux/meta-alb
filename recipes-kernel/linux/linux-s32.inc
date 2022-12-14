DESCRIPTION = "Linux kernel for S32 platforms"
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

# make everything compatible for the time being
COMPATIBLE_MACHINE_$MACHINE = "$MACHINE"
