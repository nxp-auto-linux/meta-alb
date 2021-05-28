DM_VERITY_IMAGE ?= "fsl-image-base"
DM_VERITY_IMAGE_TYPE = "ext3"
IMAGE_CLASSES += "dm-verity-img"

INITRAMFS_IMAGE = "dm-verity-image-initramfs"
INITRAMFS_FSTYPES = "cpio.gz cpio.gz.u-boot"
INITRAMFS_IMAGE_BUNDLE = "1"

IMAGE_BOOT_FILES_remove = "Image"
IMAGE_BOOT_FILES_append += "Image-initramfs-${MACHINE}.bin;Image"
IMAGE_BOOT_FILES_append += "${@os.path.basename(d.getVar("KERNEL_DEVICETREE"))}"

WKS_FILE = "s32gen1-sdimage-verity.wks.in"