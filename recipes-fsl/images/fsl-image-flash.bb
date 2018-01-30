#
# Copyright 2018 NXP
#

require images/fsl-image-base.bb
inherit image_types_fsl_flashimage

IMAGE_FSTYPES = "flashimage"

COMPATIBLE_MACHINE = "s32v234evb|s32v234pcie"
