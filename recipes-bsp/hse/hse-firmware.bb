# Copyright 2023 NXP

SUMMARY = "HSE Firmware"
LICENSE = "NXP-S32-PLATFORM-SOFTWARE"
LIC_FILES_CHKSUM = "file://${S}/${HSE_PKG}/${HSE_LIC};md5=${HSE_LIC_MD5}"
NO_GENERIC_LICENSE[NXP-S32-PLATFORM-SOFTWARE] = "${S}/${HSE_PKG}/${HSE_LIC}"

COMPATIBLE_MACHINE = "s32cc"

HSE_MACHINE:s32g2 = "S32G2"
HSE_VERSION:s32g2 ?= "0_1_0_5"
HSE_LIC:s32g2 ?= "license.rtf"
HSE_LIC_MD5:s32g2 ?= "5e04b181052bbe88c948390d15a35074"
HSE_SOC_REV:s32g2 ?= ""

HSE_MACHINE:s32g3 = "S32G3"
HSE_VERSION:s32g3 ?= "0_2_16_1"
HSE_LIC:s32g3 ?= "license.rtf"
HSE_LIC_MD5:s32g3 ?= "0474bb8a03b7bc0ac59e9331d5be687f"
HSE_SOC_REV:s32g3 ?= "rev1.0"

HSE_MACHINE:s32r45evb = "S32R45"
HSE_VERSION:s32r45evb ?= "0_1_0_4"
HSE_LIC:s32r45evb ?= "license.txt"
HSE_LIC_MD5:s32r45evb ?= "50209a0d1e14b47fb0f2be58e040930f"
HSE_SOC_REV:s32r45evb ?= ""

HSE_PKG = "HSE_FW_${HSE_MACHINE}_${HSE_VERSION}"

HSE_LOCAL_DIR ?= "${NXP_FIRMWARE_LOCAL_DIR}/${HSE_PKG}"
SRC_URI = " \
	file://${HSE_LOCAL_DIR}/hse/bin/ \
	file://${HSE_LOCAL_DIR}/interface/ \
	file://${HSE_LOCAL_DIR}/${HSE_LIC} \
	"

S = "${WORKDIR}/${NXP_FIRMWARE_LOCAL_DIR}"

HSE_FW_DIR = "${D}${base_libdir}/firmware"
HSE_INT_DIR = "${D}${includedir}/hse-interface"

do_install() {
	install -d ${HSE_FW_DIR}
	install -d ${HSE_INT_DIR}

	install -m 0644 ${S}/${HSE_PKG}/hse/bin/${HSE_SOC_REV}*.bin.pink ${HSE_FW_DIR}/s32cc_hse_fw.bin
	cp -r ${S}/${HSE_PKG}/interface/ ${HSE_INT_DIR}
}

FILES:${PN} += "${base_libdir}/firmware"
FILES:${PN} += "${includedir}/hse-interface"
