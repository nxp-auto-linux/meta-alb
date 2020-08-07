require conf/machine/include/s32-base.inc
require conf/machine/include/s32-gmac.inc

SOC_FAMILY_append = ":gen1"

UBOOT_SUFFIX ?= "s32"

IMAGE_FSTYPES += "ext3"

# Specific definitions for .sdcard image generation
IMAGE_FSTYPES += "sdcard"
SDCARD_ROOTFS = "ext3"

UBOOT_SUFFIX_SDCARD = "${UBOOT_SUFFIX}"
IMAGE_CLASSES += "image_types_fsl_sdcard"

# Permit later to optionally add flashimage to IMAGE_FSTYPES
IMAGE_CLASSES += "image_types_fsl_flashimage"

UBOOT_ENV_SDCARD_OFFSET ?= "0x001e0000"
