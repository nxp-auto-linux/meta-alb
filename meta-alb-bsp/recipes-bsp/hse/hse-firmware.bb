# Copyright 2023 NXP
#

SUMMARY = "HSE Firmware"
LICENSE = "NXP-S32-PLATFORM-SOFTWARE"
LIC_FILES_CHKSUM = "file://${S}/${HSE_PKG}/${HSE_LIC};md5=${HSE_LIC_MD5}"
NO_GENERIC_LICENSE[NXP-S32-PLATFORM-SOFTWARE] = "${HSE_PKG}/${HSE_LIC}"

COMPATIBLE_MACHINE = "s32cc|s32n"

HSE_INCLUDE_PATH = "recipes-bsp/hse"
HSE_INCLUDE:s32g2 = "${HSE_INCLUDE_PATH}/hse-firmware-s32g2-defs.inc"
HSE_INCLUDE:s32g3 = "${HSE_INCLUDE_PATH}/hse-firmware-s32g3-defs.inc"
HSE_INCLUDE:s32r45 = "${HSE_INCLUDE_PATH}/hse-firmware-s32r45-defs.inc"
HSE_INCLUDE:s32n = "${HSE_INCLUDE_PATH}/hse-firmware-s32n7-defs.inc"
require ${@d.getVar('HSE_INCLUDE') or ''}

HSE_PKG = "HSE_FW_${HSE_MACHINE}_${HSE_VERSION}"

HSE_LOCAL_DIR ?= "${NXP_FIRMWARE_LOCAL_DIR}/${HSE_PKG}"
SRC_URI = " \
	${@f"""file://{d.getVar('HSE_LOCAL_DIR')}/hse/bin/ """ if os.path.exists(f"""{d.getVar('HSE_LOCAL_DIR')}/hse/bin/""") else ''} \
	${@f"""file://{d.getVar('HSE_LOCAL_DIR')}/interface/ """ if os.path.exists(f"""{d.getVar('HSE_LOCAL_DIR')}/interface/""") else ''} \
	${@f"""file://{d.getVar('HSE_LOCAL_DIR')}/{d.getVar('HSE_LIC')} """ if os.path.exists(f"""{d.getVar('HSE_LOCAL_DIR')}/{d.getVar('HSE_LIC')}""") else ''} \
	"

S = "${WORKDIR}/${NXP_FIRMWARE_LOCAL_DIR}"

HSE_FW_DIR = "${D}${base_libdir}/firmware"
HSE_INT_DIR = "${D}${includedir}/hse-interface"

do_install() {
	install -d ${HSE_FW_DIR}
	install -d ${HSE_INT_DIR}

	cp -r ${S}/${HSE_PKG}/interface/ ${HSE_INT_DIR}
}

do_install:append:s32cc() {
	install -m 0644 ${S}/${HSE_PKG}/hse/bin/${HSE_SOC_REV}*.bin.pink ${HSE_FW_DIR}/s32cc_hse_fw.bin
}

FILES:${PN} += "${base_libdir}/firmware"
FILES:${PN} += "${includedir}/hse-interface"
