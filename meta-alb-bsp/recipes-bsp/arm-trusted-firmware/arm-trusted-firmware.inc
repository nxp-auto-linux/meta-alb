# Copyright 2019-2022, 2024 NXP

DESCRIPTION = "ARM Trusted Firmware"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://license.rst;md5=1dd070c98a281d18d9eefd938729b031"

DEPENDS:append = " bc-native"
DEPENDS:append = " coreutils-native"
DEPENDS:append = " binutils-native"
DEPENDS:append = " dtc-native xxd-native"
DEPENDS:append = " openssl-native"
DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'secboot', 'mbedtls hse-firmware', '', d)}"
DEPENDS:append = " u-boot-s32"
DEPENDS:append:s32cc = " u-boot-tools-native"
DEPENDS:append:s32cc = " ${@ 'u-boot-tools-scmi-native' if d.getVar('SCMI_DTB_NODE_CHANGE') else ''}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit deploy

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

RSA_PRIV_BL2 ??= ""
RSA_PRIV_BL31 ??= ""
RSA_PRIV_BL32 ??= ""
RSA_PRIV_BL33 ??= ""
BL2_HANDLE ??= ""
BL31_HANDLE ??= ""
BL32_HANDLE ??= ""
BL33_HANDLE ??= ""

BUILD_TYPE ?= "release"

ATF_BINARIES = "${B}/${ATF_PLAT}/${BUILD_TYPE}"
ATF_DTB = "${@'${KERNEL_DEVICETREE}' if d.getVar('KERNEL_DEVICETREE_OVERRIDE') == '' else '${KERNEL_DEVICETREE_OVERRIDE}'}"

OPTEE_ARGS = " \
                BL32=${DEPLOY_DIR_IMAGE}/optee/tee-header_v2.bin \
                BL32_EXTRA1=${DEPLOY_DIR_IMAGE}/optee/tee-pager_v2.bin \
                SPD=opteed \
                "

M7BOOT_ARGS = " \
                FIP_OFFSET_DELTA=0x2000 \
                "

SCPRT_ARGS = " \
                S32CC_USE_SCP=1 \
                FIP_ALIGN=64 \
                BL2_BASE=0x34610000 \
                BL2_LIMIT=0x346FFFFF \
                "

HSE_ARGS = " \
              HSE_SUPPORT=1 \
              "

SECBOOT_ARGS = " \
                 SECBOOT_SUPPORT=1 \
                 BL2_KEY=${RSA_PRIV_BL2} \
                 BL31_KEY=${RSA_PRIV_BL31} \
                 BL32_KEY=${RSA_PRIV_BL32} \
                 BL33_KEY=${RSA_PRIV_BL33} \
                 BL31_HSE_KEYHANDLE=${BL31_HANDLE} \
                 BL32_HSE_KEYHANDLE=${BL32_HANDLE} \
                 BL33_HSE_KEYHANDLE=${BL33_HANDLE} \
                 NXP_HSE_FWDIR=${STAGING_INCDIR}/hse-interface \
                 MBEDTLS_DIR=${RECIPE_SYSROOT}/usr/share/mbedtls-source \
                 "

QUICK_BOOT_ARGS = " \
              S32CC_COMPILE_FLAG=-Ofast \
              LOG_LEVEL=20 \
              "

NO_DRAM_ECC_ARGS = " \
              S32GEN1_DRAM_INLINE_ECC=0 \
              "

EXTRA_OEMAKE:append = " \
                CROSS_COMPILE=${TARGET_PREFIX} \
                ARCH=${TARGET_ARCH} \
                BUILD_BASE=${B} \
                PLAT=${ATF_PLAT} \
                DTB_FILE_NAME=${@os.path.basename(d.getVar('ATF_DTB'))} \
                "

EXTRA_OEMAKE:append = " ${@oe.utils.conditional('BUILD_TYPE', 'debug', 'DEBUG=1', '', d)}"
EXTRA_OEMAKE:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'optee', '${OPTEE_ARGS}', '', d)}"
EXTRA_OEMAKE:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'm7boot', '${M7BOOT_ARGS}', '', d)}"

EXTRA_OEMAKE:append = ' HOSTCC="${BUILD_CC} ${BUILD_CPPFLAGS} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}"'
EXTRA_OEMAKE:append = ' OPENSSL_DIR="${STAGING_LIBDIR_NATIVE}/../" \
                 HOSTSTRIP=true '
EXTRA_OEMAKE:append:s32cc = " ${@bb.utils.contains('DISTRO_FEATURES', 'scprt', '${SCPRT_ARGS}', '', d)}"
EXTRA_OEMAKE:append:s32cc = " ${@bb.utils.contains('DISTRO_FEATURES', 'hse', '${HSE_ARGS}', '', d)}"
EXTRA_OEMAKE:append:s32cc = " ${@bb.utils.contains('DISTRO_FEATURES', 'secboot', '${SECBOOT_ARGS}', '', d)}"
EXTRA_OEMAKE:append:s32cc = "${@bb.utils.contains('QUICK_BOOT_CONFIG', 'nodramecc', '${NO_DRAM_ECC_ARGS}', '', d)}"
EXTRA_OEMAKE:append:s32cc = "${@ "${QUICK_BOOT_ARGS}" if d.getVar('QUICK_BOOT_CONFIG', True) else ''}"
#
# Switch to SCMI versions for pinctrl and NVMEM if it's the case
EXTRA_OEMAKE:append:s32cc = " S32CC_USE_SCMI_PINCTRL=${SCMI_USE_SCMI_PINCTRL}"
EXTRA_OEMAKE:append:s32cc = " S32CC_USE_SCMI_NVMEM=${SCMI_USE_SCMI_NVMEM}"

# Switch the UART baudrate to be used if it is the case
EXTRA_OEMAKE:append = " ${@ 'S32_LINFLEX_BAUDRATE=' + d.getVar("SERIAL_CONSOLES", True).split(" ")[0].split(";")[0] }"

PINCTRL_OPT:s32cc = "${@oe.utils.conditional('SCMI_USE_SCMI_PINCTRL', '1', '--pinctrl', '--no-pinctrl', d)}"
GPIO_OPT:s32cc = "${@oe.utils.conditional('SCMI_USE_SCMI_GPIO', '1', '--gpio', '--no-gpio', d)}"
NVMEM_OPT:s32cc = "${@oe.utils.conditional('SCMI_USE_SCMI_NVMEM', '1', '--nvmem', '--no-nvmem', d)}"

PLAT_HEADERS_TAGS:s32cc = " s32gen1 s32cc"
PLAT_HEADERS_TAGS:append:s32g := " s32g"
PLAT_HEADERS_TAGS:append:s32r := " s32r45"

SECBOOT_INC = ""
VERIFIEDBOOT_INC = ""

SECBOOT_INC:s32cc = 'recipes-bsp/arm-trusted-firmware/atf-hse-secboot.inc'
VERIFIEDBOOT_INC:s32cc = 'recipes-bsp/arm-trusted-firmware/atf-hse-verifiedboot.inc'

include ${@bb.utils.contains('DISTRO_FEATURES', 'secboot', '${SECBOOT_INC}', '', d)}
include ${@bb.utils.contains('DISTRO_FEATURES', 'verifiedboot', '${VERIFIEDBOOT_INC}', '', d)}

do_configure:append() {
	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS

	oe_runmake -C "${S}" clean
}

do_compile:s32cc() {
	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS

	if [ "${SCMI_DTB_NODE_CHANGE}" = "true" ] ; then
		oe_runmake -C "${S}" dtbs
		dtb_name="${ATF_BINARIES}/fdts/$(basename ${ATF_DTB})"
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
	install -d "${DEPLOYDIR}"
}

do_deploy:append:s32cc() {
	for suffix in ${BOOT_TYPE}; do
		cp -vf "${ATF_BINARIES}/fip.s32-${suffix}" ${DEPLOYDIR}
	done
}

do_install:append:s32cc() {
	install -d ${D}${includedir}/plat-hdrs
	for tag in ${PLAT_HEADERS_TAGS}
	do
		# Export SCMI headers
		for proto in clock reset perf nvmem
		do
			hdr=$(find ${S} -name "*${tag}-*scmi*${proto}.h")
			if [ -z "${hdr}" ]
			then
				continue
			fi

			install -m 0644 "${hdr}" ${D}${includedir}/plat-hdrs
		done

		# Export other platform headers
		for name in can-ts
		do
			hdr=$(find ${S} -name "*${tag}-*${name}*.h")
			if [ -z "${hdr}" ]
			then
				continue
			fi

			install -m 0644 "${hdr}" ${D}${includedir}/plat-hdrs
		done
	done
}

FILES:${PN} = ""
ALLOW_EMPTY:${PN} = "1"

FILES:${PN}-plat-hdrs = " ${includedir}/plat-hdrs/*.h "
PROVIDES:append:s32cc = " ${PN}-plat-hdrs"
PACKAGES:append:s32cc = " ${PN}-plat-hdrs"

addtask deploy after do_compile

do_compile[depends] = "virtual/bootloader:do_deploy"
do_compile[depends] += "${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'optee-os:do_deploy', '', d)}"

COMPATIBLE_MACHINE = "s32"
