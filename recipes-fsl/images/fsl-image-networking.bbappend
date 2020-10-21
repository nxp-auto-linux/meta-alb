
IMAGE_ROOTFS_DEP_EXT ??= "ext2.gz"

# Use this intermediate variable, otherwise IMAGE_ROOTFS_DEP_EXT
# is not expanded on IMAGE_FSTYPES_append
ROOTFS_IMAGE_FSTYPE := "${IMAGE_ROOTFS_DEP_EXT}"

IMAGE_FSTYPES_remove = " ext2.gz.u-boot jffs2 ubi"
IMAGE_FSTYPES_append = " ${ROOTFS_IMAGE_FSTYPE}"
