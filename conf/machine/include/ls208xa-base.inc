# Provides the common settings for LS208xA

require conf/machine/include/soc-family.inc

SOC_FAMILY_prepend = "fsl-lsch3:ls2:ls208xa:"

IMAGE_FSTYPES = "tar.gz ext3 ext3.gz"

EXTRA_IMAGECMD_ext3 = "-i 4096 -L ${IMAGE_NAME}"

EXTRA_IMAGEDEPENDS += "dpl-examples management-complex ppa"

# No better place for this if it should stay in the layer
PREFERRED_PROVIDER_jpeg ?= "libjpeg-turbo"
PREFERRED_PROVIDER_jpeg-native ?= "jpeg-native"

# For all other elements we use the SDK defaults
require conf/machine/include/qoriq-arm64.inc
require conf/machine/include/arm/arch-arm64.inc

DTB_LOAD = "0x90000000"
UBOOT_ENTRYPOINT = "0x80080000"

# We also want to permit SD card image generation.
# This sets up the defaults, just in case the image
# specifies an addition of "sdcard" to IMAGE_FSTYPES.
# Note that we can not boot U-Boot from SD Card on BB Classic
IMAGE_CLASSES += "image_types_fsl_sdcard"
IMAGE_CLASSES += "image_types_fsl_itb image_types_fsl_flashimage"
