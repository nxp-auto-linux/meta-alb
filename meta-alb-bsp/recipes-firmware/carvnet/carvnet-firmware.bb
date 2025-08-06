# Copyright 2025 NXP

SUMMARY = "Firmware binaries for CAR-V-NET"
LICENSE = "NXP-S32-PLATFORM-SOFTWARE"
LIC_FILES_CHKSUM = "file://license.rtf;md5=bafc2fc12f6ba76334b95f6ea4844b01"
NO_GENERIC_LICENSE[NXP-S32-PLATFORM-SOFTWARE] = "license.rtf"
FILESEXTRAPATHS:prepend := "${NXP_FIRMWARE_LOCAL_DIR}/S32N7_CARVNET_FDK:"

#
# This recipe installs all precompiled CARVNET firmware examples from the CARVNET FDK release package.
# It expects content of the CARVNET FDK release package in the folder $NXP_FIRMWARE_LOCAL_DIR/S32N7_CARVNET_FDK/ .
# Based on the value of CARVNET_FW_SET_USED, one firmware example can be selected as the 'actively used' CARVNET firmware.
#
CARVNET_FW_SET_USED ?= "netc_reflector"

CARVNET_LICENSE_FILE = "license.rtf"
CARVNET_FW_FILE = "s32n7-carvnet.fw"
CARVNET_SRCDIR = "firmware"
CARVNET_INSTALL_DIR = "${base_libdir}/${CARVNET_SRCDIR}"
CARVNET_MORE_FW_DIR = "${CARVNET_INSTALL_DIR}/carvnet_firmware_examples"
FW_INSTALL_DEPLOY  = "nxp_fw/S32N7_CARVNET_FDK/"
BB_STRICT_CHECKSUM = "0"

SRC_URI = " \
    file://${CARVNET_SRCDIR} \
    file://${CARVNET_LICENSE_FILE} \
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
COMPATIBLE_MACHINE = "s32n7sim"

