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
DEPENDS:append:s32cc = "${@ ' u-boot-tools-scmi-native' if d.getVar('SCMI_DTB_NODE_CHANGE') else ''}"

KERNEL_CC:append = " ${TOOLCHAIN_OPTIONS}"
KERNEL_LD:append = " ${TOOLCHAIN_OPTIONS}"

PINCTRL_OPT = "${@oe.utils.conditional('SCMI_USE_SCMI_PINCTRL', '1', '--pinctrl', '--no-pinctrl', d)}"
GPIO_OPT = "${@oe.utils.conditional('SCMI_USE_SCMI_GPIO', '1', '--gpio', '--no-gpio', d)}"
NVMEM_OPT = "${@oe.utils.conditional('SCMI_USE_SCMI_NVMEM', '1', '--nvmem', '--no-nvmem', d)}"

PLAT_HEADERS_TAGS:s32cc = " s32gen1 s32cc"
PLAT_HEADERS_TAGS:append:s32g = " s32g"
PLAT_HEADERS_TAGS:append:s32r45 = " s32r45"
PLAT_HEADERS_TAGS:append:s32r478 = " s32r478"

# Update SYSROOT_DIRS when installing to non-standard paths, otherwise files won't be staged
SYSROOT_DIRS:append = " ${includedir}/plat-hdrs/linux"

S = "${WORKDIR}/git"


require ${@'recipes-kernel/linux/linux-s32-ext-dts.inc' if d.getVar('KERNEL_DEVICETREE_EXT') else ''}

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
        elif [ -f "${WORKDIR}/recipe-sysroot/kcfg/${deltacfg}" ]; then
            ${S}/scripts/kconfig/merge_config.sh -m .config \
                ${WORKDIR}/recipe-sysroot/kcfg/${deltacfg}
        else
            bbwarn "${deltacfg} not found!"
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

    # DTB might have been changed from an earlier build, see SCMI_DTB_NODE_CHANGE changes.
    # Clean-up these changes to guarantee an updated DTB.
	dtb_name="${B}/arch/arm64/boot/dts/${KERNEL_DEVICETREE}"
	if [ -f ${dtb_name} ]; then
	    rm ${dtb_name}
	fi
}

# Switch from SIUL2 to SCMI drivers if needed
do_compile:append() {
	if [ "${SCMI_DTB_NODE_CHANGE}" = "true" ]; then
		nativepython3 ${STAGING_BINDIR_NATIVE}/scmi_dtb_node_change.py \
			${dtb_name} \
			${GPIO_OPT} \
			${PINCTRL_OPT} \
            ${NVMEM_OPT}
	fi
}

# Cannot prepend next function since some kernel versions clean after themselves during modules_install while others do not
# Instead, we need to re-define the whole function and check for folder existance before trying to delete it
kernel_do_install() {
        #
        # First install the modules
        #
        unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS MACHINE
        if (grep -q -i -e '^CONFIG_MODULES=y$' .config); then
                oe_runmake DEPMOD=echo MODLIB=${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION} INSTALL_FW_PATH=${D}${nonarch_base_libdir}/firmware modules_install
                rm -f "${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/build"
                rm -f "${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/source"
                if [ -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel ]; then
                        # Remove empty module directories to prevent QA issues
                        find "${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel" -type d -empty -delete
                fi
        else
                bbnote "no modules to install"
        fi

        #
        # Install various kernel output (zImage, map file, config, module support files)
        #
        install -d ${D}/${KERNEL_IMAGEDEST}

        #
        # When including an initramfs bundle inside a FIT image, the fitImage is created after the install task
        # by do_assemble_fitimage_initramfs.
        # This happens after the generation of the initramfs bundle (done by do_bundle_initramfs).
        # So, at the level of the install task we should not try to install the fitImage. fitImage is still not
        # generated yet.
        # After the generation of the fitImage, the deploy task copies the fitImage from the build directory to
        # the deploy folder.
        #

        for imageType in ${KERNEL_IMAGETYPES} ; do
                if [ $imageType != "fitImage" ] || [ "${INITRAMFS_IMAGE_BUNDLE}" != "1" ] ; then
                        install -m 0644 ${KERNEL_OUTPUT_DIR}/$imageType ${D}/${KERNEL_IMAGEDEST}/$imageType-${KERNEL_VERSION}
                fi
        done

        install -m 0644 System.map ${D}/${KERNEL_IMAGEDEST}/System.map-${KERNEL_VERSION}
        install -m 0644 .config ${D}/${KERNEL_IMAGEDEST}/config-${KERNEL_VERSION}
        install -m 0644 vmlinux ${D}/${KERNEL_IMAGEDEST}/vmlinux-${KERNEL_VERSION}
        [ -e Module.symvers ] && install -m 0644 Module.symvers ${D}/${KERNEL_IMAGEDEST}/Module.symvers-${KERNEL_VERSION}
        install -d ${D}${sysconfdir}/modules-load.d
        install -d ${D}${sysconfdir}/modprobe.d
}


do_kernel_localversion:append:s32() {
        [ -f ${S}/.scmversion ] && sed -i "s/+p[0-9]//g" ${S}/.scmversion
}


do_install:append:s32() {
    if [ -f "arch/${ARCH}/boot/zImage" ]; then
        install -m 0644 arch/${ARCH}/boot/zImage ${D}/boot/zImage-${KERNEL_VERSION}
        ln -sf  zImage-${KERNEL_VERSION} ${D}/boot/zImage
    fi
}

do_install:append:s32cc() {
	install -d ${D}${includedir}/plat-hdrs/linux

	for tag in ${PLAT_HEADERS_TAGS}
	do
		# Export SCMI headers
		for proto in clock reset perf nvmem
		do
			hdr=$(find ${S}/include -name "*${tag}-*scmi*${proto}.h")
			if [ -z "${hdr}" ]; then
				continue
			fi

			install -m 0644 "${hdr}" ${D}${includedir}/plat-hdrs/linux
		done

		# Export other platform headers
		for name in can-ts
		do
			hdr=$(find ${S}/include -name "*${tag}-*${name}*.h")
			if [ -z "${hdr}" ]; then
				continue
			fi

			install -m 0644 "${hdr}" ${D}${includedir}/plat-hdrs/linux
		done
	done
}

FILES:${PN}-plat-hdrs = " ${includedir}/plat-hdrs/linux "
PROVIDES:append:s32cc = " ${PN}-plat-hdrs"
PACKAGES:append:s32cc = " ${PN}-plat-hdrs"

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

FILES:${KERNEL_PACKAGE_NAME}-image += "\
    ${sysconfdir} \
    ${sysconfdir}/modules-load.d \
    ${sysconfdir}/modprobe.d \
"

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

require quick-boot.inc
require vnet-s32.inc
require llce.inc
require ${@bb.utils.contains('DISTRO_FEATURES', 'verifiedboot', 'recipes-kernel/linux/verifiedboot.inc', '', d)}

# Since Kernel version 4.19, task 'do_merge_delta_config' requires that the cross
# compiler is available in recipe's sysroot. In order to avoid any erros/warnings
# at build time of the Linux Kernel version 5.4, we add this dependency.
do_merge_delta_config[depends] += "virtual/${TARGET_PREFIX}gcc:do_populate_sysroot"

DEPENDS = "flex-native bison-native bc-native yocto-cfg-fragments"

# Enable Xen booting
require ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'recipes-kernel/linux/linux-yocto_virtualization.inc', '', d)}
DELTA_KERNEL_DEFCONFIG:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'xen_s32.cfg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'cfg/xen.cfg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen virtio', 'xen_virtio.cfg', '', d)} \
    "
SRC_URI += "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'file://build/xen_s32.cfg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen virtio', 'file://build/xen_virtio.cfg', '', d)} \
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

SRC_URI:append:s32g = "${@ ' file://build/0001-linux-s32g-Update-.dtsi-to-configure-UART1-on-EVB3.patch' if '1' in d.getVar("SERIAL_CONSOLES", True).split(" ")[0].split(";")[1][-1] else ''}"
