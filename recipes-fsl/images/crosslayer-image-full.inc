
require recipes-fsl/images/fsl-image-networking-full.bb

# Fix dependency deficiency in fsl-image-networking-full
do_rootfs[depends] += "fsl-image-networking:do_image_complete"
