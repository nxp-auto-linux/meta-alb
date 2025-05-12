# Copyright 2025 NXP

SUMMARY = "Firmware binaries for PFE"
LICENSE = "NXP-S32-PLATFORM-SOFTWARE"
LIC_FILES_CHKSUM = "file://license.rtf;md5=bafc2fc12f6ba76334b95f6ea4844b01"
NO_GENERIC_LICENSE[NXP-S32-PLATFORM-SOFTWARE] = "license.rtf"
FILESEXTRAPATHS:prepend := "${NXP_FIRMWARE_LOCAL_DIR}:"

PFE_CLASS_FILE = "s32g_pfe_class.fw"
PFE_UTIL_FILE = "s32g_pfe_util.fw"
PFE_LICENSE_FILE = "license.rtf"

SRC_URI = " \
    ${@f"""file://{d.getVar('PFE_CLASS_FILE')} """ if os.path.exists(f"""{d.getVar('PFE_CLASS_FILE')}""") else ''} \
    ${@f"""file://{d.getVar('PFE_UTIL_FILE')} """ if os.path.exists(f"""{d.getVar('PFE_UTIL_FILE')}""") else ''} \
    ${@f"""file://{d.getVar('PFE_LICENSE_FILE')} """ if os.path.exists(f"""{d.getVar('PFE_LICENSE_FILE')}""") else ''} \
"

inherit deploy
S = "${WORKDIR}"

FW_INSTALL_DIR ?= "${base_libdir}/firmware"

do_install() {
    install -d ${D}${FW_INSTALL_DIR}
    install -m 644 ${S}/${PFE_CLASS_FILE} ${D}${FW_INSTALL_DIR}
    install -m 644 ${S}/${PFE_UTIL_FILE} ${D}${FW_INSTALL_DIR}
}

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 644 ${D}${FW_INSTALL_DIR}/${PFE_CLASS_FILE} ${DEPLOYDIR}
    install -m 644 ${D}${FW_INSTALL_DIR}/${PFE_UTIL_FILE} ${DEPLOYDIR}
}

FILES:${PN} += "${base_libdir}/firmware/${PFE_CLASS_FILE}"
FILES:${PN} += "${base_libdir}/firmware/${PFE_UTIL_FILE}"

# avoid "QA Issue: Architecture did not match" caused by firmware
INSANE_SKIP:${PN} += "arch already-stripped"

addtask deploy after do_install
COMPATIBLE_MACHINE = "s32g"
