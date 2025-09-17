# Copyright 2025 NXP

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

TFA_DTS_FILE = "${@ (d.getVar('KERNEL_DEVICETREE_OVERRIDE') or '').replace('.dtb', '.dts') }"

SRC_URI:append = "\
    file://${TFA_DTS_FILE} \
    "

EXTRA_OEMAKE:append = " \
    DDR_START=0x880000000 \
    DDR_END=0x8FFFFFFFF \
    S32N_PL011_MODULE=1 \
    PLATFORM_CLUSTER_COUNT=1 \
    "
