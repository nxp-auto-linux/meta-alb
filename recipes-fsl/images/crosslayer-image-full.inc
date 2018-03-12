require recipes-fsl/images/crosslayer-rootfs-full.inc
require recipes-fsl/images/${IMAGE_ROOTFS_REQ}.bb

# Fix dependency deficiency in fsl-image-networking-full
do_rootfs[depends] += "${IMAGE_ROOTFS_DEP}:do_image_complete"
