# Copyright 2025 NXP

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

KERNEL_DTS_FILE = "${@ (d.getVar('KERNEL_DEVICETREE_EXT') or '').replace('.dtb', '.dts') }"

SRC_URI:append = "\
    file://${KERNEL_DTS_FILE} \
    "
