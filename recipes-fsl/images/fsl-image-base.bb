# Copyright 2014-2016 Freescale
# Copyright 2017-2020 NXP
#
# The minimal rootfs with basic packages for boot up
#

BASE_CORE_IMAGE ?= "recipes-core/images/core-image-minimal.bb"
include ${BASE_CORE_IMAGE}

IMAGE_INSTALL += " \
    kernel-image \
    kernel-modules \
    setserial \
    dhcp-client \
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
IMAGE_INSTALL_append_s32g274aevb = " sja1105 "
IMAGE_INSTALL_append_s32g274ardb = " sja1105 "

# We want to have an itb to boot from in the /boot directory to be flexible
# about U-Boot behavior
IMAGE_INSTALL_append_fsl-lsch3 += " \
    linux-kernelitb-norootfs-image \
"

IMAGE_FSTYPES ?= "tar.gz"

IMAGE_INSTALL_append_gen1 = "${@bb.utils.contains('DISTRO_FEATURES', 'gmac', ' ${GMAC_IMAGE_INSTALL} ', '', d)}"

require ${@bb.utils.contains('DISTRO_FEATURES', 'pfe', 'recipes-fsl/images/fsl-image-pfe.inc', '', d)}

# Enable Xen and add Xen Packages
require ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'recipes-fsl/images/fsl-image-xen.inc', '', d)}

# S32V234 camera card support
IMAGE_INSTALL_append_s32v234campp = " init-net"

