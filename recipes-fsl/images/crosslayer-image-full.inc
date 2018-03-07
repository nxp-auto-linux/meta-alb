#
# Really magical hack to overcome a deficiency in SDK 2.0.
# fsl-image-minimal.bb is included and does a hard override
# of our IMAGE_FSTYPES setting. Overriding that image
# is a bit tricky due to inheritance mechanisms, which
# would require us to also override the other fsl-image-*
# definitions depending on it. For BlueBox we can solve
# it a different way by preserving the original value
# around it and then building the resulting value.
# This of course assumes that we don't have other hard
# overrides that would mess up the result.
# And this of course happens in fsl-image-full.bb.
# So we also hack around that one. *sigh*
#
IMAGE_FSTYPESTEMP := "${IMAGE_FSTYPES}"

require recipes-fsl/images/fsl-image-networking-full.bb

# Fix dependency deficiency in fsl-image-networking-full
do_rootfs[depends] += "fsl-image-networking:do_image_complete"

IMAGE_FSTYPES := "${IMAGE_FSTYPESTEMP} ${IMAGE_FSTYPES}"
IMAGE_FSTYPES_qoriq := "${IMAGE_FSTYPESTEMP} ${IMAGE_FSTYPES_qoriq}"
