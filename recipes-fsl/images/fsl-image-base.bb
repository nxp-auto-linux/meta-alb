# Copyright 2014-2016 Freescale
# Copyright 2017-2018 NXP
#
# The minimal rootfs with basic packages for boot up
#

include recipes-core/images/core-image-minimal.bb

IMAGE_INSTALL += " \
    kernel-image \
    kernel-modules \
    setserial \
"

# Add pciutils package
IMAGE_INSTALL += "pciutils"

# Add iputils package, for ping support
IMAGE_INSTALL += "iputils"

# For LS2, dpl-examples, management-complex, restool is a machine dependency.
# rcw is dependency for image_types_fsl_flashimage.
# restool is also added for other LS2 flavors, except ls2084*. Leave it commented by now.
#IMAGE_INSTALL_append_fsl-lsch3 += " \
#    restool \
#"

# Support for SJA1105 swich under Linux
IMAGE_INSTALL_append_s32v234evb = " sja1105 "
IMAGE_INSTALL_append_s32v234bbmini = " sja1105 "

IMAGE_FSTYPES ?= "tar.gz"
