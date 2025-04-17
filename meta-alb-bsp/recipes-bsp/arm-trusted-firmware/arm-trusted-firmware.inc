# Copyright 2019-2022, 2024-2026 NXP

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
DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'skip-uboot', 'linux-s32', '', d)}"
DEPENDS:append:s32cc = " u-boot-tools-native"
DEPENDS:append:s32cc = " ${@ 'u-boot-tools-scmi-native' if d.getVar('SCMI_DTB_NODE_CHANGE') else ''}"
DEPENDS:append:s32g = " ddr-firmware"
DEPENDS:append:s32r45 = " ddr-firmware"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit deploy

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

TFA_BUILD_ARTIFACTS_DIR = "TFA_Build_Artifacts"

HSE_CNT_TABLE_ADDR ??= ""

BL2_VERS ??= ""

BL31_NVCTR ??= ""
BL32_NVCTR ??= ""
BL33_NVCTR ??= ""
DDRFW_NVCTR ??= ""

RSA_PRIV_BL2 ??= ""
RSA_PRIV_BL31 ??= ""
RSA_PRIV_BL32 ??= ""
RSA_PRIV_BL33 ??= ""
RSA_PRIV_DDRFW ??= ""
BL2_HANDLE ??= ""
BL31_HANDLE ??= ""
BL32_HANDLE ??= ""
BL33_HANDLE ??= ""
DDRFW_HANDLE ??= ""
RPMB_HANDLE ??= ""

BUILD_TYPE ?= "release"

TFA_BINARIES = "${B}/${TFA_PLAT}/${BUILD_TYPE}"
TFA_OUTPUT = "${B}/${TFA_PLAT}"

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
                 DDR_HSE_KEYHANDLE=${BL2_HANDLE} \
                 BL31_HSE_KEYHANDLE=${BL31_HANDLE} \
                 BL32_HSE_KEYHANDLE=${BL32_HANDLE} \
                 BL33_HSE_KEYHANDLE=${BL33_HANDLE} \
                 NXP_HSE_FWDIR=${STAGING_INCDIR}/hse-interface \
                 MBEDTLS_DIR=${RECIPE_SYSROOT}/usr/share/mbedtls-source \
                 "

RP_SECBOOT_ARGS = " \
                    MMC_RPMB_SUPPORT=1 \
                    BL2_VERSION=${BL2_VERS} \
                    TFW_NVCTR_VAL=${BL31_NVCTR} \
                    TOS_NVCTR_VAL=${BL32_NVCTR} \
                    NTFW_NVCTR_VAL=${BL33_NVCTR} \
                    DDRFW_NVCTR_VAL=${DDRFW_NVCTR} \
                    S32CC_MONOTONIC_CNT_SUPPORT=1 \
                    RPMB_KEY_HSE_KEYHANDLE=${RPMB_HANDLE} \
                    RPMB_MONOTONIC_CNT_ADDR=${HSE_CNT_TABLE_ADDR} \
                    "

QUICK_BOOT_ARGS = " \
              S32CC_COMPILE_FLAG=-Ofast \
              LOG_LEVEL=20 \
              "

S32CC_LINUX_KERNEL_AS_BL33_ARGS = " \
              BL33_ENTRYPOINT=0x81000000 \
              S32_BL33_IMAGE_SIZE=0x01000000 \
			  S32_LINUX_KERNEL_AS_BL33=1 \
              "

NO_DRAM_ECC_ARGS = " \
              S32GEN1_DRAM_INLINE_ECC=0 \
              "

FSS_ARGS = " \
	S32N_USE_FSS=1 \
	"

EXTRA_OEMAKE:append = " \
                CROSS_COMPILE=${TARGET_PREFIX} \
                ARCH=${TARGET_ARCH} \
                BUILD_BASE=${B} \
                PLAT=${TFA_PLAT} \
                DTB_FILE_NAME=${TFA_DEVICETREE} \
                "

EXTRA_OEMAKE:append = " ${@oe.utils.conditional('BUILD_TYPE', 'debug', 'DEBUG=1', '', d)}"
EXTRA_OEMAKE:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'optee', '${OPTEE_ARGS}', '', d)}"

EXTRA_OEMAKE:append = ' HOSTCC="${BUILD_CC} ${BUILD_CPPFLAGS} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}"'
EXTRA_OEMAKE:append = ' OPENSSL_DIR="${STAGING_LIBDIR_NATIVE}/../" \
                 HOSTSTRIP=true '
EXTRA_OEMAKE:append:s32g = " ${@bb.utils.contains('DISTRO_FEATURES', 'scprt', '${SCPRT_ARGS}', '', d)}"
EXTRA_OEMAKE:append:s32cc = " ${@bb.utils.contains('DISTRO_FEATURES', 'hse', '${HSE_ARGS}', '', d)}"
EXTRA_OEMAKE:append:s32cc = " ${@bb.utils.contains('DISTRO_FEATURES', 'secboot', '${SECBOOT_ARGS}', '', d)}"
EXTRA_OEMAKE:append:s32cc = " ${@bb.utils.contains('DISTRO_FEATURES', 'rpsecboot', '${RP_SECBOOT_ARGS}', '', d)}"
EXTRA_OEMAKE:append:s32cc = "${@bb.utils.contains('QUICK_BOOT_CONFIG', 'nodramecc', '${NO_DRAM_ECC_ARGS}', '', d)}"
EXTRA_OEMAKE:append:s32cc = "${@ "${QUICK_BOOT_ARGS}" if d.getVar('QUICK_BOOT_CONFIG', True) else ''}"
#
# Switch to SCMI versions for pinctrl and NVMEM if it's the case
EXTRA_OEMAKE:append:s32cc = " S32CC_USE_SCMI_PINCTRL=${SCMI_USE_SCMI_PINCTRL}"
EXTRA_OEMAKE:append:s32cc = " S32CC_USE_SCMI_NVMEM=${SCMI_USE_SCMI_NVMEM}"

# Provide path for the platform specific DDR firmware binary
EXTRA_OEMAKE:append:s32g = " DDR_FW_BIN_PATH=${STAGING_BASELIBDIR}/ddr-firmware/ddr_fw_ecc_off.bin"
EXTRA_OEMAKE:append:s32r45 = " DDR_FW_BIN_PATH=${STAGING_BASELIBDIR}/ddr-firmware/ddr_fw_ecc_off.bin"

# Switch the UART to be used if it is the case
EXTRA_OEMAKE:append:s32cc = " ${@ 'S32_LINFLEX_MODULE=' + d.getVar("SERIAL_CONSOLES", True).split(" ")[0].split(";")[1][-1] }"

# Switch the UART baudrate to be used if it is the case
EXTRA_OEMAKE:append:s32cc = " ${@ 'S32_LINFLEX_BAUDRATE=' + d.getVar("SERIAL_CONSOLES", True).split(" ")[0].split(";")[0] }"

# Use the Linux Kernel Image as BL33 and skip U-boot entirely
LINUX_KERNEL_AS_BL33 = "${@bb.utils.contains('DISTRO_FEATURES', 'skip-uboot', '1', '0', d)}"
EXTRA_OEMAKE:append:s32cc = " ${@bb.utils.contains('DISTRO_FEATURES', 'skip-uboot', '${S32CC_LINUX_KERNEL_AS_BL33_ARGS}', '', d)}"

EXTRA_OEMAKE:append:s32r478 = " ${S32R47_ARGS}"
EXTRA_OEMAKE:append:s32n = " ${@bb.utils.contains('DISTRO_FEATURES', 'fss', '${FSS_ARGS}', '', d)}"

PINCTRL_OPT:s32cc = "${@oe.utils.conditional('SCMI_USE_SCMI_PINCTRL', '1', '--pinctrl', '--no-pinctrl', d)}"
GPIO_OPT:s32cc = "${@oe.utils.conditional('SCMI_USE_SCMI_GPIO', '1', '--gpio', '--no-gpio', d)}"
NVMEM_OPT:s32cc = "${@oe.utils.conditional('SCMI_USE_SCMI_NVMEM', '1', '--nvmem', '--no-nvmem', d)}"

PLAT_HEADERS_TAGS:s32cc = " s32gen1 s32cc"
PLAT_HEADERS_TAGS:append:s32g = " s32g"
PLAT_HEADERS_TAGS:append:s32r45 = " s32r45"
PLAT_HEADERS_TAGS:append:s32r478 = " s32r478"

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
		dtb_name="${TFA_BINARIES}/fdts/${TFA_DEVICETREE}"
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

		if [ "${LINUX_KERNEL_AS_BL33}" = "1" ] ; then
			BL33="${DEPLOY_DIR_IMAGE}/Image"
		else
			BL33="${DEPLOY_DIR_IMAGE}/u-boot-nodtb.bin-${suffix}"
		fi

		oe_runmake -C "${S}" \
			BL33="${BL33}" \
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

do_compile:s32n() {
	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS

	for suffix in ${BOOT_TYPE}
	do
		if [ "${suffix}" = "sdcard" ] ; then
			s32n_boot_src="mmc"
		else
			s32n_boot_src="${suffix}"
		fi
		oe_runmake -C "${S}" clean
		oe_runmake -C "${S}" \
		    BL33="${DEPLOY_DIR_IMAGE}/u-boot-nodtb.bin-${suffix}" \
		    S32N_BOOT_SRC=${s32n_boot_src} all
		cp -vf "${TFA_BINARIES}/bl2_w_dtb.bin" "${TFA_OUTPUT}/bl2_w_dtb.bin-${suffix}"
		cp -vf "${TFA_BINARIES}/bl2/bl2.elf" "${TFA_OUTPUT}/bl2.elf-${suffix}"
		cp -vf "${TFA_BINARIES}/bl31/bl31.elf" "${TFA_OUTPUT}/bl31.elf-${suffix}"
		cp -vf "${TFA_BINARIES}/fip.bin" "${TFA_OUTPUT}/fip.bin-${suffix}"
	done
}

do_deploy:append:s32cc() {
	for suffix in ${BOOT_TYPE}; do
		cp -vf "${TFA_BINARIES}/bl2_w_dtb.s32-${suffix}" ${DEPLOYDIR}
		cp -vf "${TFA_BINARIES}/fip.bin-${suffix}" ${DEPLOYDIR}
	done
}

do_deploy:append:s32r478() {
	file="bl2_build_artifacts"
	tfa_compile_log="${WORKDIR}/temp/log.do_compile"

	install -d "${DEPLOYDIR}/${TFA_BUILD_ARTIFACTS_DIR}"
	printf "TFA BL2 SW Build Time Info:\n\n" > "${file}"
	printf "QuadSPI BL2 Flash offset: ${TFA_RESET_OFFSET}\n" >> "${file}"
	printf "QuadSPI BL2 Flash partition size: ${TFA_RESET_SIZE}\n" >> "${file}"
	grep "Boot Core:" "${tfa_compile_log}" >> "${file}"
	grep "IVT Location:" "${tfa_compile_log}" >> "${file}"
	grep "Load address:" "${tfa_compile_log}" >> "${file}"
	grep "Entry point:" "${tfa_compile_log}" >> "${file}"
	cp -vf "${file}" "${DEPLOYDIR}/${TFA_BUILD_ARTIFACTS_DIR}"

	for suffix in ${BOOT_TYPE}; do
		bl2_size=$(stat -c %s "${TFA_BINARIES}/bl2_w_dtb.bin-${suffix}")
		reminder=$(expr "$bl2_size" % 8)

		if [ "$reminder" -ne 0 ]; then
			padding=$(expr 8 - "$reminder")
			dd if=/dev/zero of="${TFA_BINARIES}/bl2_w_dtb.bin-${suffix}" bs=1 count="$padding" seek="$bl2_size"
		fi

		cp -vf "${TFA_BINARIES}/bl2_w_dtb.bin-${suffix}" ${DEPLOYDIR}
	done
}

do_deploy:append:s32n7sim() {
	tfa_deploy_dir="${DEPLOYDIR}/simimage/arm-trusted-firmware"

	install -d "${tfa_deploy_dir}"

	for suffix in ${BOOT_TYPE}; do
		# Save bins
		install -v "${TFA_OUTPUT}/bl2_w_dtb.bin-${suffix}" "${tfa_deploy_dir}/"
		# Save elfs for symbols
		install -v "${TFA_OUTPUT}/bl2.elf-${suffix}" "${tfa_deploy_dir}/"
		install -v "${TFA_OUTPUT}/bl31.elf-${suffix}" "${tfa_deploy_dir}/"
		install -v "${TFA_OUTPUT}/fip.bin-${suffix}" "${tfa_deploy_dir}/"
	done
	install -v "${TFA_BINARIES}/fdts/s32n79-simulator.dtb" "${tfa_deploy_dir}/"
}

do_install:append:s32cc() {
	install -d ${D}${includedir}/plat-hdrs/tf-a

	for tag in ${PLAT_HEADERS_TAGS}
	do
		# Export SCMI headers
		for proto in clock reset perf nvmem
		do
			hdr=$(find ${S} -name "*${tag}-*scmi*${proto}.h")
			if [ -z "${hdr}" ]; then
				continue
			fi

			install -m 0644 "${hdr}" ${D}${includedir}/plat-hdrs/tf-a
		done

		# Export other platform headers
		for name in can-ts
		do
			hdr=$(find ${S} -name "*${tag}-*${name}*.h")
			if [ -z "${hdr}" ]; then
				continue
			fi

			install -m 0644 "${hdr}" ${D}${includedir}/plat-hdrs/tf-a
		done
	done
}

FILES:${PN} = ""
ALLOW_EMPTY:${PN} = "1"

FILES:${PN}-plat-hdrs = " ${includedir}/plat-hdrs/tf-a "
PROVIDES:append:s32cc = " ${PN}-plat-hdrs"
PACKAGES:append:s32cc = " ${PN}-plat-hdrs"

addtask deploy after do_compile

do_configure[depends] = "virtual/bootloader:do_deploy"
do_compile[depends] += "${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'optee-os:do_deploy', '', d)}"
do_compile[depends] += "${@bb.utils.contains('DISTRO_FEATURES', 'skip-uboot', 'linux-s32:do_deploy', '', d)}"

SRC_URI:append:s32g = "${@ ' file://0001-tf-a-s32g-Update-dtsi-to-configure-UART1-on-EVB3.patch' if '1' in d.getVar("SERIAL_CONSOLES", True).split(" ")[0].split(";")[1][-1] else ''}"

COMPATIBLE_MACHINE = "s32"
