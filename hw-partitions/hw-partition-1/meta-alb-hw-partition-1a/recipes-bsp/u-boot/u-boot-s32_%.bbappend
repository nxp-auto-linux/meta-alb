# Copyright 2025 NXP

FILESEXTRAPATHS:prepend := "${THISDIR}:"

SRC_URI:append = "\
    file://build/config-1a.cfg \
    "

DELTA_UBOOT_DEFCONFIG:append = " config-1a.cfg"
