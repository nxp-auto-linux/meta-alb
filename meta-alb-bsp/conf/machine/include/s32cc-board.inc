require conf/machine/include/s32cc.inc

KERNEL_DEFCONFIG ?= "s32cc_defconfig"

EXTRA_IMAGECMD:ext4 = "-i 4096 -L ${IMAGE_NAME}"
IMAGE_FSTYPES:remove = "jffs2 ubi ext2.gz ext2.gz.u-boot"

UBOOT_BOOTSPACE_OFFSET = "0"
UBOOT_BOOTSPACE_SEEK = "0"

UBOOT_CONFIG ?= "sdcard"
UBOOT_CONFIG[sdcard] = "${UBOOT_DEFCONFIG},,u-boot-nodtb.${UBOOT_SUFFIX}"

# prepend 'qspi' to keep the default deploy/u-boot-<MACHINE>.s32 to point to sdcard
UBOOT_CONFIG:prepend = "qspi "
UBOOT_CONFIG[qspi] = "${UBOOT_FLASH_DEFCONFIG},,u-boot-nodtb.${UBOOT_SUFFIX}"

# Define dependencies for .flashimage
# Offset values are in bytes and are deduced from the values defined in
# u-boot/include/configs/s32.h as {KERNEL|FDT|RAMDISK}_FLASH_ADDR
FLASHIMAGE_UBOOT_TYPE = "qspi"
# Normally only FLASHIMAGE_UBOOT_TYPE should be enough, but we also define
# the *_SUFFIX and *_FILE for safety
FLASHIMAGE_SIZE="64"
FLASHIMAGE_UBOOT_SUFFIX = "${UBOOT_SUFFIX}"
FLASHIMAGE_KERNEL_FILE = "${@bb.utils.contains('DISTRO_FEATURES', 'verifiedboot', 'Image-kernel-signed.itb', '${KERNEL_IMAGETYPE}', d)}"
FLASHIMAGE_DTB_SUFFIX = "dtb"
FLASHIMAGE_DTB = "linux-s32"
FLASHIMAGE_ROOTFS = "fsl-image-base"
FLASHIMAGE_ROOTFS_SUFFIX = "${@bb.utils.contains('DISTRO_FEATURES', 'verifiedboot', 'cpio.gz', 'cpio.gz.u-boot', d)}"
FLASHIMAGE_ROOTFS_FILE = "${@bb.utils.contains('DISTRO_FEATURES', 'verifiedboot', 'rootfs-${MACHINE}.itb', '${FLASHIMAGE_ROOTFS}-${MACHINE}.${FLASHIMAGE_ROOTFS_SUFFIX}', d)}"

IMAGE_FSTYPES += "${FLASHIMAGE_ROOTFS_SUFFIX}"

# Add extra sdcard logical partition with rootfs image if Xen enabled
SDCARD_ROOTFS_EXTRA1_FILE ?= "${@bb.utils.contains('DISTRO_FEATURES', 'xen', '${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${SDCARD_ROOTFS_EXT}', '', d)}"
SDCARD_ROOTFS_EXTRA1_SIZE ?= "${@bb.utils.contains('DISTRO_FEATURES', 'xen', '${ROOTFS_SIZE}', '0', d)}"
SDCARD_ROOTFS_LOGICAL_EXTRA ?= "${@bb.utils.contains('DISTRO_FEATURES', 'xen', '1', '0', d)}"
