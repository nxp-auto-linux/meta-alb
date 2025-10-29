# Copyright 2025 NXP

FILESEXTRAPATHS:prepend := "${THISDIR}:"

SRC_URI:append = "\
    file://dts/s32n79-hw-partition-1.1.5.dtsi \
    "
