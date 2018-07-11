
IMAGE_FSTYPES_remove = " ext2.gz.u-boot jffs2 ubi"
IMAGE_FSTYPES_append = " ext3.gz"

# Fix dependency deficiency in fsl-image-networking-full
do_rootfs[depends] += "fsl-image-networking:do_image_complete"