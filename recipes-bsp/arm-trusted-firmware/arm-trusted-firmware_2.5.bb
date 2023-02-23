# Copyright 2019-2022 NXP

DESCRIPTION = "ARM Trusted Firmware"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://license.rst;md5=1dd070c98a281d18d9eefd938729b031"

require recipes-bsp/arm-trusted-firmware/atf-src.inc

DEPENDS += "bc-native"
DEPENDS += "coreutils-native"
DEPENDS += "binutils-native"
DEPENDS += "dtc-native xxd-native"
DEPENDS += "openssl-native"
DEPENDS += "u-boot-s32"
DEPENDS += "u-boot-tools-native"
DEPENDS += "${@ 'u-boot-tools-scmi-native' if d.getVar('SCMI_DTB_NODE_CHANGE') else ''}"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'optee-os', '', d)}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit deploy

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

BUILD_TYPE ?= "release"

ATF_BINARIES = "${B}/${ATF_PLAT}/${BUILD_TYPE}"

OPTEE_ARGS = " \
                BL32=${DEPLOY_DIR_IMAGE}/optee/tee-header_v2.bin \
                BL32_EXTRA1=${DEPLOY_DIR_IMAGE}/optee/tee-pager_v2.bin \
                SPD=opteed \
                "

XEN_ARGS = " \
                S32_HAS_HV=1 \
                "

M7BOOT_ARGS = " \
                FIP_OFFSET_DELTA=0x2000 \
                "
SCPRT_ARGS = " \
                S32CC_USE_SCP=1 \
                FIP_ALIGN=64 \
                "
HSE_ARGS = " \
              HSE_SUPPORT=1 \
	      "

EXTRA_OEMAKE += " \
                CROSS_COMPILE=${TARGET_PREFIX} \
                ARCH=${TARGET_ARCH} \
                BUILD_BASE=${B} \
                PLAT=${ATF_PLAT} \
                "

EXTRA_OEMAKE += "${@oe.utils.conditional('BUILD_TYPE', 'debug', 'DEBUG=1', '', d)}"

EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'optee', '${OPTEE_ARGS}', '', d)}"
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'xen', '${XEN_ARGS}', '', d)}"
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'm7boot', '${M7BOOT_ARGS}', '', d)}"

EXTRA_OEMAKE += 'HOSTCC="${BUILD_CC} ${BUILD_CPPFLAGS} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}"'
EXTRA_OEMAKE += 'OPENSSL_DIR="${STAGING_LIBDIR_NATIVE}/../" \
                 HOSTSTRIP=true'
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'scprt', '${SCPRT_ARGS}', '', d)}"
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'hse', '${HSE_ARGS}', '', d)}"

# Switch to SCMI versions for pinctrl and NVMEM if it's the case
EXTRA_OEMAKE += "S32CC_USE_SCMI_PINCTRL=${SCMI_USE_SCMI_PINCTRL}"
EXTRA_OEMAKE += "S32CC_USE_SCMI_NVMEM=${SCMI_USE_SCMI_NVMEM}"

PINCTRL_OPT = "${@oe.utils.conditional('SCMI_USE_SCMI_PINCTRL', '1', '--pinctrl', '--no-pinctrl', d)}"
GPIO_OPT = "${@oe.utils.conditional('SCMI_USE_SCMI_GPIO', '1', '--gpio', '--no-gpio', d)}"
NVMEM_OPT = "${@oe.utils.conditional('SCMI_USE_SCMI_NVMEM', '1', '--nvmem', '--no-nvmem', d)}"

SCMI_HEADERS_TAGS = " s32gen1 s32cc"
SCMI_HEADERS_TAGS:append:s32g := " s32g"
SCMI_HEADERS_TAGS:append:s32r := " s32r45"

BOOT_TYPE = "sdcard qspi"

SECBOOT = "${@bb.utils.contains('DISTRO_FEATURES', 'secboot', 'recipes-bsp/arm-trusted-firmware/atf-hse-secboot.inc', '', d)}"
include ${SECBOOT}

do_compile() {
	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS

	oe_runmake -C "${S}" clean

	if ${SCMI_DTB_NODE_CHANGE}; then
		oe_runmake -C "${S}" dtbs
		dtb_name="${ATF_BINARIES}/fdts/$(basename ${KERNEL_DEVICETREE})"
		nativepython3 ${STAGING_BINDIR_NATIVE}/scmi_dtb_node_change.py \
			${dtb_name} \
			${GPIO_OPT} \
			${PINCTRL_OPT} \
			${NVMEM_OPT}
	fi

	for suffix in ${BOOT_TYPE}
	do
		oe_runmake -C "${S}" \
		    BL33="${DEPLOY_DIR_IMAGE}/u-boot-nodtb.bin-${suffix}" \
		    MKIMAGE="mkimage" \
		    BL33DIR="${DEPLOY_DIR_IMAGE}/tools/" \
		    MKIMAGE_CFG="${DEPLOY_DIR_IMAGE}/tools/u-boot-s32.cfgout-${suffix}" all
		cp -vf "${ATF_BINARIES}/fip.s32" "${ATF_BINARIES}/fip.s32-${suffix}"
		cp -vf "${ATF_BINARIES}/fip.bin" "${ATF_BINARIES}/fip.bin-${suffix}"
	done
}

do_deploy() {
	install -d ${DEPLOYDIR}
	for suffix in ${BOOT_TYPE}; do
		cp -vf "${ATF_BINARIES}/fip.s32-${suffix}" ${DEPLOYDIR}
	done
}

do_install:append() {
	install -d ${D}${includedir}/scmi-hdrs
	for tag in ${SCMI_HEADERS_TAGS}
	do
		for proto in clock reset perf
		do
			hdr=$(find ${S} -name "*${tag}-*scmi*${proto}.h")
			if [ -z "${hdr}" ]
			then
				continue
			fi

			install -m 0644 "${hdr}" ${D}${includedir}/scmi-hdrs
		done
	done
}

FILES:${PN}-scmi-hdrs = " ${includedir}/scmi-hdrs/*.h "
PROVIDES += "${PN}-scmi-hdrs"
PACKAGES += "${PN}-scmi-hdrs"

addtask deploy after do_compile

do_compile[depends] = "virtual/bootloader:do_install"

COMPATIBLE_MACHINE = "s32"
