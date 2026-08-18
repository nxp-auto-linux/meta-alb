# Copyright 2025 NXP

FILESEXTRAPATHS:prepend := "${THISDIR}:"
FILESEXTRAPATHS:prepend := "${THISDIR}/../dts-ddr/${DDR_ECC_VARIANT}:"

SRC_URI:append = "\
    file://dts/s32n79-hw-partition-1.2.1.dtsi \
    file://s32n79-hw-partition-1.2.1-ddr.dtsi;subdir=dts \
    "
