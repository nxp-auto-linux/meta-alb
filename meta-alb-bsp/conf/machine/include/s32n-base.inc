# Provides common settings for S32N boards, simulation and emulation models
require conf/machine/include/s32-base.inc
require conf/machine/include/arm/armv8-2a/tune-cortexa78ae.inc

SOC_FAMILY:append = ":s32n"

DEFAULTTUNE:s32n ?= "cortexa78ae"

KERNEL_DEFCONFIG = "s32n7_defconfig"
