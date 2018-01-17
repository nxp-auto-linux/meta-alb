#
# Copyright 2017,2018 NXP
#

require images/fsl-image-minimal.bb
require images/fsl-image-core-common.inc
include images/fsl-image-s32-common.inc
include images/fsl-dpaa2.inc

inherit distro_features_check

# Increase the freespace
IMAGE_ROOTFS_EXTRA_SPACE ?= "54000"
