# Copyright 2017 NXP
require images/fsl-image-full.bb
require images/fsl-image-s32v2xx-common.inc

inherit distro_features_check

IMAGE_INSTALL_remove += "perf packagegroup-fsl-extend packagegroup-fsl-extend-misc"
IMAGE_INSTALL += "u-boot-images"
#Add packages are required for LTP
IMAGE_INSTALL += "packagegroup-fsl-ltp"
#Benchmark tools
IMAGE_INSTALL += "dhrystone ramsmp"
#Add pciutils package
IMAGE_INSTALL += "pciutils"
#Add fbv package
IMAGE_INSTALL += "fbv"

#Add packages required for OpenMPI demo
IMAGE_INSTALL += "imagemagick gnuplot mpich mpich-dev"

#Increase the freespace
IMAGE_ROOTFS_EXTRA_SPACE ?= "32000"
