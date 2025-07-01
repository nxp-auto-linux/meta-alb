# Provides common settings for S32N boards, simulation and emulation models
require conf/machine/include/s32-base.inc
require conf/machine/include/arm/armv8-2a/tune-cortexa78ae.inc

SOC_FAMILY:append = ":s32n"

DEFAULTTUNE:s32n ?= "cortexa78ae"

PREFERRED_VERSION_linux-s32:s32n = "6.12%"

KERNEL_DEFCONFIG = "s32n7_defconfig"

IMAGE_CLASSES += " \
	image_types_fsl_ufs \
	image_types_fsl_sdcard_s32n \
"

IMAGE_FSTYPES:append = " ufs sdcard_s32n"
UBOOT_SUFFIX_UFS = "${UBOOT_SUFFIX}"

BOOT_TYPE ?= "ddr ufs sdcard"

# Set to "1" when using an out-of-tree DTB for TF-A. The device tree source is expected to
# be found in the TF-A ${WORKDIR}.
TFA_DTB_EXT ?= "0"
