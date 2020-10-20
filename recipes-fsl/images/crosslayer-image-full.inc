require recipes-fsl/images/crosslayer-rootfs-full.inc

#
# Really magical hack to overcome a deficiency in meta-freescale.
# fsl-image-networking*.bb is included and does a hard override
# of our IMAGE_FSTYPES setting. Overriding that image
# is a bit tricky due to inheritance mechanisms, which
# would require us to also override the other fsl-image-*
# definitions depending on it. For meta-alb we can solve
# it a different way by preserving the original value
# around it and then building the resulting value.
# More, we are ignorring the FSTYPES from fsl-image-networking*.bb
# since we'll have to remove most of them anyway.
# This of course assumes that we don't have other hard
# overrides that would mess up the result.
#
IMAGE_FSTYPESTEMP := "${IMAGE_FSTYPES}"

require ${IMAGE_ROOTFS_REQ}.bb

# Both appends below are required. First to fix the assignment in
# fsl-image-networking, and the second to fix the qoriq override
# in fsl-image-networking-full.
IMAGE_FSTYPES += "${IMAGE_FSTYPESTEMP}"
IMAGE_FSTYPES_qoriq += "${IMAGE_FSTYPESTEMP}"

# Fix dependency deficiency in fsl-image-networking-full
do_rootfs[depends] += "${IMAGE_ROOTFS_DEP}:do_image_complete"
