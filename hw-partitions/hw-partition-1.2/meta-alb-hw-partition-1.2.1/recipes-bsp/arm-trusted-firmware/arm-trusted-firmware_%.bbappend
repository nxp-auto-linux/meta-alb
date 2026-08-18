# Copyright 2025-2026 NXP

FILESEXTRAPATHS:prepend := "${THISDIR}/dts:"
FILESEXTRAPATHS:prepend := "${THISDIR}/../dts-ddr/${DDR_ECC_VARIANT}:"

TFA_DTS_FILE = "${@ (d.getVar('KERNEL_DEVICETREE_OVERRIDE') or '').replace('.dtb', '.dts') }"

SRC_URI:append = "\
    file://s32n79-hw-partition-1.2.1.dtsi \
    file://${TFA_DTS_FILE} \
    file://s32n79-hw-partition-1.2.1-ddr.dtsi \
    "

EXTRA_OEMAKE:append = " \
    DDR_START=0x80000000 \
    DDR_END=0xFFFFFFFF \
    S32N_PL011_MODULE=0 \
    PLATFORM_CLUSTER_COUNT=4 \
    "
