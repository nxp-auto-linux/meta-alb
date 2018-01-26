require conf/machine/include/s32-base.inc

SOC_FAMILY_append = ":s32v:s32v2xx"

UBOOT_SUFFIX ?= "s32"

IMAGE_FSTYPES += "ext3"

# Specific definitions for .sdcard image generation
IMAGE_FSTYPES += "sdcard"
SDCARD_ROOTFS = "${IMGDEPLOYDIR}/${IMAGE_NAME}.rootfs.ext3"
UBOOT_TYPE_SDCARD = "${MACHINE}"
UBOOT_SUFFIX_SDCARD = "${UBOOT_SUFFIX}"
IMAGE_CLASSES += "image_types_fsl_sdcard"

UBOOT_CONFIG ?= "sdcard"
UBOOT_CONFIG[sdcard] = "${MACHINE}_config,,u-boot.${UBOOT_SUFFIX}"
UBOOT_MACHINE_SDCARD ?= "${MACHINE}"

