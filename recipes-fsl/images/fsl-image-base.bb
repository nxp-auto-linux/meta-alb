# Copyright 2014-2016 Freescale
# Copyright 2017-2020 NXP
#
# The minimal rootfs with basic packages for boot up
#

BASE_CORE_IMAGE ?= "recipes-core/images/core-image-minimal.bb"
include ${BASE_CORE_IMAGE}

DM_VERITY_IMG = "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'fsl-dm-verity.inc', '', d)}"
include ${DM_VERITY_IMG}

IMAGE_INSTALL += " \
    kernel-image \
    kernel-modules \
    setserial \
    dhcpcd \
"

# Add pciutils package
IMAGE_INSTALL += "pciutils"

# Add iputils package, for ping support
IMAGE_INSTALL += "iputils"

# For LS2/LX2, dpl-examples, management-complex, restool is a machine dependency.
# rcw is dependency for image_types_fsl_flashimage.
IMAGE_INSTALL_append_fsl-lsch3 += " \
    restool \
"

# Support for SJA1105 swich under Linux
IMAGE_INSTALL_append_s32v234evb = " sja1105 "
IMAGE_INSTALL_append_s32v234bbmini = " sja1105 "
IMAGE_INSTALL_append_s32g2evb = " sja1105 "
IMAGE_INSTALL_append_s32g274ardb = " sja1105 "

# Support for SJA1110 swich under Linux
IMAGE_INSTALL_append_s32g274ardb2 = " sja1110 "

# Support for STR (Suspend to RAM) -- rtcwake
IMAGE_INSTALL_append_s32g2 = " util-linux-rtcwake "

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

# Add LLCE CAN if needed
IMAGE_INSTALL_append_s32g2 = "${@bb.utils.contains('DISTRO_FEATURES', 'llce-can', ' linux-firmware-llce-can', '', d)}"

# S32V234 camera card support
IMAGE_INSTALL_append_s32v234campp = " init-net"

