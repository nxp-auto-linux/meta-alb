require conf/machine/include/s32-base.inc

SOC_FAMILY_append = ":gen1"

UBOOT_SUFFIX ?= "s32"

IMAGE_FSTYPES += "ext3"

# Specific definitions for .sdcard image generation
IMAGE_FSTYPES += "sdcard"
SDCARD_ROOTFS = "${IMGDEPLOYDIR}/${IMAGE_NAME}.rootfs.ext3"
UBOOT_SUFFIX_SDCARD = "${UBOOT_SUFFIX}"
IMAGE_CLASSES += "image_types_fsl_sdcard"

# Permit later to optionally add flashimage to IMAGE_FSTYPES
IMAGE_CLASSES += "image_types_fsl_flashimage"

PREFERRED_VERSION_gmac = "${@bb.utils.contains('PREFERRED_VERSION_linux-s32', '4.14', '0.4.1', '0.4.2', d)}"
# For kernel 4.14 we have a separate gmac recipe; for kernel 4.19, gmac is built with the kernel
GMAC_IMAGE_INSTALL = "${@bb.utils.contains('PREFERRED_VERSION_linux-s32', '4.14', 'gmac', '', d)}"

# Add gmac by default
DISTRO_FEATURES_append += "${@bb.utils.contains('PREFERRED_VERSION_linux-s32', '4.14', ' gmac', '', d)}"
