# Copyright 2025 NXP

FILESEXTRAPATHS:prepend := "${THISDIR}/dts:"

TFA_DTS_FILE = "${@ (d.getVar('KERNEL_DEVICETREE_OVERRIDE') or '').replace('.dtb', '.dts') }"

SRC_URI:append = "\
    file://s32n79-hw-partition-1.1.6.dtsi \
    file://${TFA_DTS_FILE} \
    "

EXTRA_OEMAKE:append = " \
    DDR_START=0x9C000000 \
    DDR_END=0xB7FFFFFF \
    S32N_PL011_MODULE=7 \
    PLATFORM_CLUSTER_COUNT=2 \
    S32N_SCMI_MBX=2 \
    "
