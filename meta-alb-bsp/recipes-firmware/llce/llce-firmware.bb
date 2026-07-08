# Copyright 2025 NXP

SUMMARY = "Firmware binaries for LLCE"
LICENSE = "NXP-S32-PLATFORM-SOFTWARE"
LIC_FILES_CHKSUM = "file://license.txt;md5=96b2b32a4c530209b2127d9973875e9a"
NO_GENERIC_LICENSE[NXP-S32-PLATFORM-SOFTWARE] = "license.txt"
FILESEXTRAPATHS:prepend := "${NXP_FIRMWARE_LOCAL_DIR}:"

LLCE_DTE_FILE     ?= "dte.bin"
LLCE_FRPE_FILE    ?= "frpe.bin"
LLCE_PPETX_FILE   ?= "ppe_tx.bin"
LLCE_PPERX_FILE   ?= "ppe_rx.bin"
LLCE_LICENSE_FILE ?= "license.txt"
FW_INSTALL_DEPLOY  = "nxp_fw"

SRC_URI = " \
    ${@f"""file://{d.getVar('LLCE_DTE_FILE')} """ if os.path.exists(f"""{d.getVar('NXP_FIRMWARE_LOCAL_DIR')}/{d.getVar('LLCE_DTE_FILE')}""") else ''} \
    ${@f"""file://{d.getVar('LLCE_FRPE_FILE')} """ if os.path.exists(f"""{d.getVar('NXP_FIRMWARE_LOCAL_DIR')}/{d.getVar('LLCE_FRPE_FILE')}""") else ''} \
    ${@f"""file://{d.getVar('LLCE_PPETX_FILE')} """ if os.path.exists(f"""{d.getVar('NXP_FIRMWARE_LOCAL_DIR')}/{d.getVar('LLCE_PPETX_FILE')}""") else ''} \
    ${@f"""file://{d.getVar('LLCE_PPERX_FILE')} """ if os.path.exists(f"""{d.getVar('NXP_FIRMWARE_LOCAL_DIR')}/{d.getVar('LLCE_PPERX_FILE')}""") else ''} \
    ${@f"""file://{d.getVar('LLCE_LICENSE_FILE')} """ if os.path.exists(f"""{d.getVar('NXP_FIRMWARE_LOCAL_DIR')}/{d.getVar('LLCE_LICENSE_FILE')}""") else ''} \
"

inherit deploy
S = "${WORKDIR}"

do_install() {
    install -d ${D}/${base_libdir}/firmware/
    install -m 0644 ${S}/${LLCE_DTE_FILE}    ${D}/${base_libdir}/firmware/${LLCE_DTE_FILE}
    install -m 0644 ${S}/${LLCE_FRPE_FILE}   ${D}/${base_libdir}/firmware/${LLCE_FRPE_FILE}
    install -m 0644 ${S}/${LLCE_PPETX_FILE}  ${D}/${base_libdir}/firmware/${LLCE_PPETX_FILE}
    install -m 0644 ${S}/${LLCE_PPERX_FILE}  ${D}/${base_libdir}/firmware/${LLCE_PPERX_FILE}
}

do_deploy() {
    install -d ${DEPLOYDIR}/${FW_INSTALL_DEPLOY}
    install -m 644 ${S}/${LLCE_LICENSE_FILE} ${DEPLOYDIR}/${FW_INSTALL_DEPLOY}/${LLCE_LICENSE_FILE}
    install -m 644 ${S}/${LLCE_DTE_FILE} ${DEPLOYDIR}/${FW_INSTALL_DEPLOY}/${LLCE_DTE_FILE}
    install -m 644 ${S}/${LLCE_FRPE_FILE} ${DEPLOYDIR}/${FW_INSTALL_DEPLOY}/${LLCE_FRPE_FILE}
    install -m 644 ${S}/${LLCE_PPETX_FILE} ${DEPLOYDIR}/${FW_INSTALL_DEPLOY}/${LLCE_PPETX_FILE}
    install -m 644 ${S}/${LLCE_PPERX_FILE} ${DEPLOYDIR}/${FW_INSTALL_DEPLOY}/${LLCE_PPERX_FILE}
}

addtask deploy after do_compile

FILES:${PN} = "${base_libdir}/firmware/dte.bin \
               ${base_libdir}/firmware/frpe.bin \
               ${base_libdir}/firmware/ppe_tx.bin \
               ${base_libdir}/firmware/ppe_rx.bin"

# Skip architecture sanity check since this is a firmware package
INSANE_SKIP:${PN} += "arch already-stripped"

COMPATIBLE_MACHINE = "s32g"