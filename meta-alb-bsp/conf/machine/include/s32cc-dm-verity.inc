DM_VERITY_IMAGE ?= "fsl-image-base"
DM_VERITY_IMAGE_TYPE = "ext3"
IMAGE_CLASSES += "dm-verity-img"

INITRAMFS_IMAGE = "dm-verity-image-initramfs"
INITRAMFS_FSTYPES = "cpio.gz cpio.gz.u-boot"
INITRAMFS_IMAGE_BUNDLE = "1"

IMAGE_BOOT_FILES:remove = "Image"
IMAGE_BOOT_FILES:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'verifiedboot', ' Image-kernel-signed.itb', ' Image-initramfs-${MACHINE}.bin;Image', d)}"
IMAGE_BOOT_FILES:append = " ${@os.path.basename(d.getVar("KERNEL_DEVICETREE"))}"

WKS_FILE = "s32cc-sdimage-verity.wks.in"
