# Copyright 2025-2026 NXP

SUMMARY = "Firmware binaries for CAR-V-NET"
LICENSE = "LA-OPT-NXP-SOFTWARE-LICENSE-V64.2"
LIC_FILES_CHKSUM = "file://license.txt;md5=96b2b32a4c530209b2127d9973875e9a"
NO_GENERIC_LICENSE[LA-OPT-NXP-SOFTWARE-LICENSE-V64.2] = "license.txt"
FILESEXTRAPATHS:prepend := "${NXP_FIRMWARE_LOCAL_DIR}/SW32N7_CARVNET:"

#
# This recipe installs all precompiled CARVNET firmware examples from the CARVNET installer.
# It expects content of the CARVNET installer in the folder $NXP_FIRMWARE_LOCAL_DIR/SW32N7_CARVNET/ .
# Based on the value of CARVNET_FW_SET_USED, one firmware example can be selected as the 'actively used' CARVNET firmware.
#
CARVNET_FW_SET_USED ?= "netc_reflector"

CARVNET_LICENSE_FILE = "license.txt"
CARVNET_FW_FILE = "s32n7-carvnet.fw"
CARVNET_SRCDIR = "carvnet_fdk/firmware"
CARVNET_INSTALL_DIR = "${base_libdir}/firmware"
CARVNET_MORE_FW_DIR = "${CARVNET_INSTALL_DIR}/carvnet_firmware_examples"
FW_INSTALL_DEPLOY  = "nxp_fw/SW32N7_CARVNET/"
BB_STRICT_CHECKSUM = "0"

SRC_URI = " \
    ${@f"""file://{d.getVar('CARVNET_SRCDIR')} """ if os.path.exists(f"""{d.getVar('NXP_FIRMWARE_LOCAL_DIR')}/SW32N7_CARVNET/{d.getVar('CARVNET_SRCDIR')}""") else ''} \
    ${@f"""file://{d.getVar('CARVNET_LICENSE_FILE')} """ if os.path.exists(f"""{d.getVar('NXP_FIRMWARE_LOCAL_DIR')}/SW32N7_CARVNET/{d.getVar('CARVNET_LICENSE_FILE')}""") else ''} \
"

S = "${WORKDIR}"

do_install() {
    install -d ${D}/${CARVNET_MORE_FW_DIR}
    install -m 0644 ${S}/${CARVNET_SRCDIR}/* ${D}/${CARVNET_MORE_FW_DIR}/
    install -m 0644 ${S}/${CARVNET_SRCDIR}/${CARVNET_FW_SET_USED} ${D}/${CARVNET_INSTALL_DIR}/${CARVNET_FW_FILE}
}

FILES:${PN} = " \
    ${CARVNET_MORE_FW_DIR} \
    ${CARVNET_INSTALL_DIR}/${CARVNET_FW_FILE} \
"

INSANE_SKIP:${PN} += "arch already-stripped"
COMPATIBLE_MACHINE = "s32n"

