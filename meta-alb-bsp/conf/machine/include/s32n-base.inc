# Provides common settings for S32N boards, simulation and emulation models
require conf/machine/include/s32-base.inc
require conf/machine/include/arm/armv8-2a/tune-cortexa78ae.inc

SOC_FAMILY:append = ":s32n"

DEFAULTTUNE:s32n ?= "cortexa78ae"

PREFERRED_VERSION_linux-s32:s32n = "6.6%"

KERNEL_DEFCONFIG = "s32n7_defconfig"

IMAGE_CLASSES += " \
	image_types_fsl_ufs \
"

IMAGE_FSTYPES += "ufs"
UBOOT_SUFFIX_UFS = "${UBOOT_SUFFIX}"

BOOT_TYPE ?= "ddr ufs"
