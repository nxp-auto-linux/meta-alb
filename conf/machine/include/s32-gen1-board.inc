require conf/machine/include/s32-gen1.inc

EXTRA_IMAGECMD_ext4 = "-i 4096 -L ${IMAGE_NAME}"
IMAGE_FSTYPES_remove = "jffs2 ubi ext2.gz ext2.gz.u-boot"

UBOOT_BOOTSPACE_OFFSET = "0"
UBOOT_BOOTSPACE_SEEK = "0"

# Define dependencies for .flashimage
# Offset values are in bytes and are deduced from the values defined in
# u-boot/include/configs/s32.h as {KERNEL|FDT|RAMDISK}_FLASH_ADDR
FLASHIMAGE_UBOOT_TYPE = "qspi"
# Normally only FLASHIMAGE_UBOOT_TYPE should be enough, but we also define
# the *_SUFFIX and *_FILE for safety
require conf/machine/include/s32gen1flashmap.inc
FLASHIMAGE_SIZE="64"
FLASHIMAGE_UBOOT_SUFFIX = "${UBOOT_SUFFIX}"
FLASHIMAGE_KERNEL_FILE = "${KERNEL_IMAGETYPE}"
FLASHIMAGE_DTB_SUFFIX = "dtb"
FLASHIMAGE_EXTRA1 = "linux-s32"
FLASHIMAGE_EXTRA1_OFFSET = "${FLASHIMAGE_DTB_OFFSET}"
FLASHIMAGE_ROOTFS = "fsl-image-base"
FLASHIMAGE_ROOTFS_SUFFIX = "cpio.gz.u-boot"
FLASHIMAGE_ROOTFS_FILE = "${DEPLOY_DIR_IMAGE}/${FLASHIMAGE_ROOTFS}-${MACHINE}.${FLASHIMAGE_ROOTFS_SUFFIX}"

IMAGE_FSTYPES += "${FLASHIMAGE_ROOTFS_SUFFIX}"

# Add extra sdcard partition with rootfs image if Xen enabled
SDCARD_ROOTFS_EXTRA1_FILE ?= "${@bb.utils.contains('DISTRO_FEATURES', 'xen', '${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${SDCARD_ROOTFS_EXT}', '', d)}"
SDCARD_ROOTFS_EXTRA1_SIZE ?= "${@bb.utils.contains('DISTRO_FEATURES', 'xen', '${ROOTFS_SIZE}', '0', d)}"
