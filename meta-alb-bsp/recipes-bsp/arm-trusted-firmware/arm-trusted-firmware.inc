# Copyright 2019-2022, 2024-2025 NXP

DESCRIPTION = "ARM Trusted Firmware"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://license.rst;md5=1dd070c98a281d18d9eefd938729b031"

DEPENDS:append = " bc-native"
DEPENDS:append = " coreutils-native"
DEPENDS:append = " binutils-native"
DEPENDS:append = " dtc-native xxd-native"
DEPENDS:append = " openssl-native"
DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'hse', 'hse-firmware', '', d)}"
DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'secboot', 'mbedtls', '', d)}"
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

TFA_BINARIES = "${B}/${TFA_PLAT}/${BUILD_TYPE}"
TFA_DTB = "${@'${KERNEL_DEVICETREE}' if d.getVar('KERNEL_DEVICETREE_OVERRIDE') == '' else '${KERNEL_DEVICETREE_OVERRIDE}'}"

OPTEE_ARGS = " \
                BL32=${DEPLOY_DIR_IMAGE}/optee/tee-header_v2.bin \
                BL32_EXTRA1=${DEPLOY_DIR_IMAGE}/optee/tee-pager_v2.bin \
                SPD=opteed \
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

S32R47_ARGS = " \
                BL2_BASE=0x90000000 \
                BL2_LIMIT=0x91000000 \
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
                PLAT=${TFA_PLAT} \
                DTB_FILE_NAME=${@os.path.basename(d.getVar('TFA_DTB'))} \
                "

EXTRA_OEMAKE:append = " ${@oe.utils.conditional('BUILD_TYPE', 'debug', 'DEBUG=1', '', d)}"
EXTRA_OEMAKE:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'optee', '${OPTEE_ARGS}', '', d)}"

EXTRA_OEMAKE:append = ' HOSTCC="${BUILD_CC} ${BUILD_CPPFLAGS} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}"'
EXTRA_OEMAKE:append = ' OPENSSL_DIR="${STAGING_LIBDIR_NATIVE}/../" \
                 HOSTSTRIP=true '
EXTRA_OEMAKE:append:s32g = " ${@bb.utils.contains('DISTRO_FEATURES', 'scprt', '${SCPRT_ARGS}', '', d)}"
EXTRA_OEMAKE:append:s32cc = " ${@bb.utils.contains('DISTRO_FEATURES', 'hse', '${HSE_ARGS}', '', d)}"
EXTRA_OEMAKE:append:s32cc = " ${@bb.utils.contains('DISTRO_FEATURES', 'secboot', '${SECBOOT_ARGS}', '', d)}"
EXTRA_OEMAKE:append:s32cc = "${@bb.utils.contains('QUICK_BOOT_CONFIG', 'nodramecc', '${NO_DRAM_ECC_ARGS}', '', d)}"
EXTRA_OEMAKE:append:s32cc = "${@ "${QUICK_BOOT_ARGS}" if d.getVar('QUICK_BOOT_CONFIG', True) else ''}"
#
# Switch to SCMI versions for pinctrl and NVMEM if it's the case
EXTRA_OEMAKE:append:s32cc = " S32CC_USE_SCMI_PINCTRL=${SCMI_USE_SCMI_PINCTRL}"
EXTRA_OEMAKE:append:s32cc = " S32CC_USE_SCMI_NVMEM=${SCMI_USE_SCMI_NVMEM}"

# Switch the UART to be used if it is the case
EXTRA_OEMAKE:append:s32cc = " ${@ 'S32_LINFLEX_MODULE=' + d.getVar("SERIAL_CONSOLES", True).split(" ")[0].split(";")[1][-1] }"

# Switch the UART baudrate to be used if it is the case
EXTRA_OEMAKE:append:s32cc = " ${@ 'S32_LINFLEX_BAUDRATE=' + d.getVar("SERIAL_CONSOLES", True).split(" ")[0].split(";")[0] }"

EXTRA_OEMAKE:append:s32r478 = " ${S32R47_ARGS}"
EXTRA_OEMAKE:append:s32r478 = " ${@bb.utils.contains('VALIDATION_HW', 'S32R47_W_S32R45', 'S32R47_W_S32R45=1', '', d)}"
EXTRA_OEMAKE:append:s32r478 = " ${@bb.utils.contains('VALIDATION_HW', 'S32R47_W_S32G2', 'S32R47_W_S32G2=1', '', d)}"

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

do_configure:append:s32cc() {

	mkdir -p ${WORKDIR}/tools

	# Update the path of 'HSE_FW' in u-boot-s32.cfgout-* to be relative to arm-trusted-firmware's
	# sysroot. Otherwise, during compilation, arm-trusted-firmware might try to load the hse firmware
	# binary from a path that does not exist anymore. Do not touch the deployed files, but instead
	# copy and update them in the current workdir.
	
	for suffix in ${BOOT_TYPE}
	do
		cp -vf ${DEPLOY_DIR_IMAGE}/tools/u-boot-s32.cfgout-${suffix} ${WORKDIR}/tools

		if ${@bb.utils.contains('DISTRO_FEATURES', 'hse', 'true', 'false', d)}; then
        		hse_fw=\""${STAGING_BASELIBDIR}/firmware/s32cc_hse_fw.bin\""
			sed -i "s,\(HSE_FW \).*,\1${hse_fw}," ${WORKDIR}/tools/u-boot-s32.cfgout-${suffix}
		fi
	done
}

do_compile:s32cc() {
	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS

	if [ "${SCMI_DTB_NODE_CHANGE}" = "true" ] ; then
		oe_runmake -C "${S}" dtbs
		dtb_name="${TFA_BINARIES}/fdts/$(basename ${TFA_DTB})"
		nativepython3 ${STAGING_BINDIR_NATIVE}/scmi_dtb_node_change.py \
			${dtb_name} \
			${GPIO_OPT} \
			${PINCTRL_OPT} \
			${NVMEM_OPT}
	fi

	for suffix in ${BOOT_TYPE}
	do
		if [ "${suffix}" = "sdcard" ] ; then
			fip_location="mmc"
		else
			fip_location="${suffix}"
		fi

		oe_runmake -C "${S}" \
		    BL33="${DEPLOY_DIR_IMAGE}/u-boot-nodtb.bin-${suffix}" \
		    MKIMAGE="mkimage" \
		    BL33DIR="${DEPLOY_DIR_IMAGE}/tools/" \
		    MKIMAGE_CFG="${WORKDIR}/tools/u-boot-s32.cfgout-${suffix}" \
		    FIP_LOCATION="${fip_location}" all
		cp -vf "${TFA_BINARIES}/bl2_w_dtb.s32" "${TFA_BINARIES}/bl2_w_dtb.s32-${suffix}"
		cp -vf "${TFA_BINARIES}/bl2_w_dtb.bin" "${TFA_BINARIES}/bl2_w_dtb.bin-${suffix}"
		cp -vf "${TFA_BINARIES}/fip.bin" "${TFA_BINARIES}/fip.bin-${suffix}"
		# If secure boot is used, copy BL2 signature for each boot type
		if [ -f "${TFA_BINARIES}/bl2-signature.bin" ]; then
			cp -vf "${TFA_BINARIES}/bl2-signature.bin" "${TFA_BINARIES}/bl2-signature.bin-${suffix}"
		fi
	done
}

do_deploy() {
	install -d "${DEPLOYDIR}"
}

do_deploy:append:s32cc() {
	for suffix in ${BOOT_TYPE}; do
		cp -vf "${TFA_BINARIES}/bl2_w_dtb.s32-${suffix}" ${DEPLOYDIR}
		cp -vf "${TFA_BINARIES}/fip.bin-${suffix}" ${DEPLOYDIR}
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

do_configure[depends] = "virtual/bootloader:do_deploy"
do_compile[depends] += "${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'optee-os:do_deploy', '', d)}"

SRC_URI:append:s32g = "${@ ' file://0001-tf-a-s32g-Update-dtsi-to-configure-UART1-on-EVB3.patch' if '1' in d.getVar("SERIAL_CONSOLES", True).split(" ")[0].split(";")[1][-1] else ''}"

COMPATIBLE_MACHINE = "s32"
