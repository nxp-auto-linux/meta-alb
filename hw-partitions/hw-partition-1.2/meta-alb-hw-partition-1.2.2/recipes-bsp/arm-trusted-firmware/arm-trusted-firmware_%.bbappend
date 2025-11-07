# Copyright 2025 NXP

FILESEXTRAPATHS:prepend := "${THISDIR}/dts:"

TFA_DTS_FILE = "${@ (d.getVar('KERNEL_DEVICETREE_OVERRIDE') or '').replace('.dtb', '.dts') }"

SRC_URI:append = "\
    file://s32n79-hw-partition-1.2.2.dtsi \
    file://${TFA_DTS_FILE} \
    "

EXTRA_OEMAKE:append = " \
    DDR_START=0x80000000 \
    DDR_END=0xFFFFFFFF \
    S32N_PL011_MODULE=0 \
    PLATFORM_CLUSTER_COUNT=4 \
    S32N_SCMI_MBX=0 \
    "
