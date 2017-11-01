# Provides the common settings for S32V2xx

SOC_FAMILY = "s32v:s32v2xx"

require conf/machine/include/soc-family.inc
require conf/machine/include/arm/arch-arm64.inc

# S32V BSP default providers
PREFERRED_PROVIDER_u-boot ?= "u-boot-s32v2xx"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-s32v2xx"
PREFERRED_PROVIDER_virtual/kernel = "linux-s32v2xx"

KERNEL_IMAGETYPE ?= "Image"

UBOOT_MACHINE_SDCARD ?= "${MACHINE}"
UBOOT_MACHINE ?= "${MACHINE}_config"

#s32v has no hardware clock
MACHINE_FEATURES_BACKFILL_CONSIDERED = "rtc"

MACHINE_FEATURES = "pci ext2 ext2 serial"
MACHINE_EXTRA_RRECOMMENDS = "udev-extraconf"

# We want to be on the safe side
BBMASK_append = "|.*llvm2.8|.*llvm2.9"

# We also want to permit SD card image generation
# This sets up the defaults, just in case the image
# specifies an addition of "sdcard" to IMAGE_FSTYPES
# Due to some limitations, we can't just use
# image_types_fsl but have to duplicate it mostly.
IMAGE_CLASSES += "image_types_fsl_sdcard"

#Fix warning for ITS file 
KERNEL_ITS_FILE ?= "."

# Add mirrors for linaro urls
LINARO_URL = "https?://releases.linaro.org/${MMYY}/"
LINARO_ARCHIVE_URL = "https://releases.linaro.org/archive/${MMYY}/"
LINARO_SNAPSHOT_URL = "https://snapshots.linaro.org/openembedded/sources/"
ABE_URL = "http://148.251.136.42/snapshots-ref/"

MIRRORS += " \
        ${LINARO_URL} ${LINARO_ARCHIVE_URL} \n \
        ${LINARO_URL}.* ${LINARO_SNAPSHOT_URL} \n \
        ${LINARO_URL}.* ${ABE_URL} \n \
"

# Include downloads folder
FSL_LOCAL_MIRROR ?= "file://${TOPDIR}/../downloads"

# Specific jpeg package
PREFERRED_PROVIDER_jpeg = "libjpeg-turbo"
PREFERRED_PROVIDER_jpeg-native = "libjpeg-turbo-native"

# Using gdb 7.11.1
GDBVERSION_s32v2xx = "7.11.1"
