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


# S32V BSP default providers
PREFERRED_PROVIDER_u-boot ?= "u-boot-s32"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-s32"
PREFERRED_PROVIDER_virtual/kernel = "linux-s32"

# ??? TODO to check
#IMAGE_FSTYPES += "ext3 sdcard"
#SDCARD_ROOTFS ?= "${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.ext3"
#IMAGE_FSTYPES_remove = "jffs2 ubi ext2.gz ext2.gz.u-boot"

#IMAGE_CLASSES += "image_types_uboot"

SOC_DEFAULT_IMAGE_FSTYPES = "tar.gz"
IMAGE_FSTYPES ?= "${SOC_DEFAULT_IMAGE_FSTYPES}"

MACHINE_FEATURES ?= "pci ext2 ext3 serial"
MACHINE_EXTRA_RRECOMMENDS += "udev-extraconf kernel-modules"

EXTRA_IMAGEDEPENDS += "u-boot cst-native"

# Sub-architecture support
MACHINE_SOCARCH_SUFFIX ?= ""
MACHINE_SOCARCH_SUFFIX_s32 = "-s32"

MACHINE_ARCH_FILTER = "virtual/kernel"

INHERIT += "fsl-dynamic-packagearch"

