require conf/machine/include/soc-family.inc
require conf/machine/include/arm/arch-arm64.inc

SOC_FAMILY = "s32"

# Provides the common settings for S32 targets
# Machines may include this file directly, if little to no changes are
# required for their definitions.
# Otherwise, intermediate include files should be
# created for various families (e.g. s32v-base.inc, s32g-base.inc etc)

KERNEL_IMAGETYPE ?= "Image"

UBOOT_MACHINE ?= "${MACHINE}_config"
UBOOT_MACHINE_SDCARD ?= "${MACHINE}"

# S32 BSP default providers
PREFERRED_PROVIDER_u-boot ?= "u-boot-s32"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-s32"
PREFERRED_PROVIDER_virtual/kernel = "linux-s32"

UBOOT_BOOTSPACE_SEEK ?= "8"
UBOOT_SUFFIX ?= "s32"
UBOOT_KERNEL_IMAGETYPE ?= "Image"
UBOOT_ENTRYPOINT ?= "80080000"

# we do not want to have getty running on tty1 as we run
# auto-serial-console there USE_VT = "0"
SERIAL_CONSOLE = "115200 ttyLF0"

SOC_DEFAULT_IMAGE_FSTYPES = "tar.gz"
IMAGE_FSTYPES ?= "${SOC_DEFAULT_IMAGE_FSTYPES}"

IMAGE_FSTYPES += "ext3"
#IMAGE_CLASSES += "image_types_uboot"

# Specific definitions for .sdcard image generation
IMAGE_FSTYPES += "sdcard"
SDCARD_ROOTFS = "${IMGDEPLOYDIR}/${IMAGE_NAME}.rootfs.ext3"
UBOOT_TYPE_SDCARD = "${MACHINE}"
UBOOT_SUFFIX_SDCARD = "${UBOOT_SUFFIX}"
IMAGE_CLASSES += "image_types_fsl_sdcard"

MACHINE_FEATURES ?= "pci ext2 ext3 serial"
MACHINE_EXTRA_RRECOMMENDS += "udev-extraconf kernel-modules"

EXTRA_IMAGEDEPENDS += "u-boot cst-native"

# Sub-architecture support
MACHINE_SOCARCH_SUFFIX ?= ""
MACHINE_SOCARCH_SUFFIX_s32 = "-s32"

MACHINE_ARCH_FILTER = "virtual/kernel"

INHERIT += "fsl-dynamic-packagearch"
