# Copyright 2025 NXP

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

TFA_DTS_FILE = "${@ (d.getVar('KERNEL_DEVICETREE_OVERRIDE') or '').replace('.dtb', '.dts') }"

SRC_URI:append = "\
    file://${TFA_DTS_FILE} \
    "

EXTRA_OEMAKE:append = " \
    DDR_START=0x9C000000 \
    DDR_END=0xB7FFFFFF \
    S32N_PL011_MODULE=7 \
    PLATFORM_CLUSTER_COUNT=2 \
    S32N_SCMI_MBX=2 \
    "
