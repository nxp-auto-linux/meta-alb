# Copyright 2017 NXP
require images/fsl-image-core.bb
require images/fsl-image-s32v2xx-common.inc

inherit distro_features_check

#Increase the freespace
IMAGE_ROOTFS_EXTRA_SPACE ?= "54000"
