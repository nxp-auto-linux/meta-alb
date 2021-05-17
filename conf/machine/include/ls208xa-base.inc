# Provides the common settings for LS208xA

require conf/machine/include/soc-family.inc

SOC_FAMILY = "fsl-lsch3:ls2:ls208xa"

IMAGE_FSTYPES = "tar.gz ext4"

# required for sdcard deploy
HOSTTOOLS += "parted mkfs.vfat mcopy udevadm"

EXTRA_IMAGECMD_ext4 = "-i 4096 -L ${IMAGE_NAME}"

EXTRA_IMAGEDEPENDS += "mc-utils management-complex ppa"

PREMIRRORS_prepend = " git://github.com/qoriq-open-source/ git://source.codeaurora.org/external/qoriq/qoriq-components/ \n "

# No better place for this if it should stay in the layer
PREFERRED_PROVIDER_jpeg ?= "libjpeg-turbo"
PREFERRED_PROVIDER_jpeg-native ?= "jpeg-native"


# Add and force using version 10.24.0, latest available with LS2084 support
# 10.24.1 from meta-qoriq does not have LS2084 support
PREFERRED_VERSION_management-complex = "10.24.0"

# Add and force using version 10.24.0, latest available with LS2084 support
# 10.24.1 from meta-qoriq does not have LS2084 support
PREFERRED_VERSION_management-complex = "10.24.0"

PREFERRED_VERSION_rng-tools ?= "5"
PREFERRED_VERSION_tcpreplay = "4.3.3"
PREFERRED_VERSION_dpdk = "19.11-20.04"

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
SDCARD_ROOTFS ?= "ext4"
UBOOT_SUFFIX_SDCARD ?= "${UBOOT_SUFFIX}"

# 64 MB boot partition
BOOT_SPACE ?= "65536"

# Other types of images
IMAGE_CLASSES += "image_types_fsl_itb image_types_fsl_flashimage"

# Deploy rootfs type for bluebox images
IMAGE_ROOTFS_DEP_EXT ?= "ext4"
