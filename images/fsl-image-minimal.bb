# Copyright 2014-2016 Freescale
# Copyright 2017,2018 NXP
#
# The minimal rootfs with basic packages for boot up
#

include recipes-core/images/core-image-minimal.bb

DEPLOY_PKGS = ""

DEPLOY_PKGS_append_fsl-lsch3 = " \
    dpl-examples \
    management-complex \
    rcw \
    u-boot \
"

EXTRA_IMAGEDEPENDS_append_fsl-lsch3 = " ${DEPLOY_PKGS}"

IMAGE_INSTALL += " \
    kernel-image \
    setserial \
"

IMAGE_INSTALL_append_fsl-lsch3 += " \
    restool \
"

IMAGE_FSTYPES ?= "tar.gz"
