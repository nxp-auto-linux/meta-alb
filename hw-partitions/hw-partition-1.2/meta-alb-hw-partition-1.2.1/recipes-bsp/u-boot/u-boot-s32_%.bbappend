# Copyright 2025 NXP

FILESEXTRAPATHS:prepend := "${THISDIR}:"

UBOOT_CONFIG_FILE:s32n79sim = "config-sim-1.2.1.cfg"
UBOOT_CONFIG_FILE:s32n79rdb = "config-rdb-1.2.1.cfg"

SRC_URI:append = "\
    file://build/${UBOOT_CONFIG_FILE} \
    "

DELTA_UBOOT_DEFCONFIG:append = " ${UBOOT_CONFIG_FILE}"
