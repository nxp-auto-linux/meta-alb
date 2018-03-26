
IMAGE_FSTYPES_remove = "ext2.gz.u-boot jffs2 ubi"

# Fix dependency deficiency in fsl-image-networking-full
do_rootfs[depends] += "fsl-image-networking:do_image_complete"