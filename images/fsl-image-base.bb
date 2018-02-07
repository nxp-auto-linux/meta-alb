# Copyright 2014-2016 Freescale
# Copyright 2017-2018 NXP
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

# Add pciutils package
IMAGE_INSTALL += "pciutils"

# Add iputils package, for ping support
IMAGE_INSTALL += "iputils"

IMAGE_INSTALL_append_fsl-lsch3 += " \
    restool \
"

# Support for SJA1105 swich under Linux
IMAGE_INSTALL_append_s32v234evb = " sja1105 "
IMAGE_INSTALL_append_s32v234bbmini = " sja1105 "

IMAGE_FSTYPES ?= "tar.gz"
