#
# Copyright 2017-2018 NXP
#

require images/fsl-image-base.bb
require images/fsl-image-core-common.inc
include images/fsl-image-s32-common.inc
include images/fsl-dpaa2.inc

inherit distro_features_check

# copy the manifest and the license text for each package to image
COPY_LIC_MANIFEST = "1"
COPY_LIC_DIRS = "1"

IMAGE_INSTALL += " \
    kernel-devicetree \
    packagegroup-core-buildessential \
    packagegroup-core-nfs-server \
    packagegroup-core-tools-debug \
"

# Benchmark tools
IMAGE_INSTALL += "dhrystone ramsmp"

# Add packages required for OpenMPI demo
# TODO: add them to the RDEPENDS list in the OpenMPI demo recipe
IMAGE_INSTALL += "imagemagick gnuplot mpich mpich-dev"

# Increase the freespace
IMAGE_ROOTFS_EXTRA_SPACE ?= "54000"
