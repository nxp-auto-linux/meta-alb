# Copyright 2014-2016 Freescale
# Copyright 2017-2023 NXP
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
    rng-tools \
    udev-extraconf \
"

# Add pciutils package
IMAGE_INSTALL += "pciutils"

# Add iputils package, for ping support
IMAGE_INSTALL += "iputils"

# For LS2/LX2, dpl-examples, management-complex, restool is a machine dependency.
# rcw is dependency for image_types_fsl_flashimage.
IMAGE_INSTALL:append:fsl-lsch3 = " \
    restool \
"

# Support for SJA1110 swich under Linux
IMAGE_INSTALL:append:s32g274ardb2 = " sja1110 "
IMAGE_INSTALL:append:s32g399ardb3 = " sja1110 "

# Support for STR (Suspend to RAM) -- rtcwake
IMAGE_INSTALL:append:s32g = " util-linux-rtcwake "

# Export QSPI FLash script
IMAGE_INSTALL:append:s32 = " linux-qspi-tool "

# Export MMC Linux Script test utilitary
# To be used with 'fsl-image-flash' image
IMAGE_INSTALL:append = "${@bb.utils.contains('DISTRO_FEATURES', 'bsp-utils', ' mmc-test-tool', '', d)}"

# We want to have an itb to boot from in the /boot directory to be flexible
# about U-Boot behavior
IMAGE_INSTALL:append:fsl-lsch3 = " \
    linux-kernelitb-norootfs-image \
"

IMAGE_FSTYPES ?= "tar.gz"

# Populate PFE and PFE FW
require ${@bb.utils.contains('DISTRO_FEATURES', 'pfe', 'recipes-fsl/images/fsl-image-pfe.inc', '', d)}

# Populate PFE slave driver
IMAGE_INSTALL:append:s32g = "${@bb.utils.contains('DISTRO_FEATURES', 'pfe-slave', ' pfe-slave', '', d)}"

# Enable Xen and add Xen Packages
require ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'recipes-fsl/images/fsl-image-xen.inc', '', d)}

# Add OP-TEE user-space components
IMAGE_INSTALL:append:s32cc = "${@bb.utils.contains('DISTRO_FEATURES', 'optee', ' optee-client optee-examples optee-test ', '', d)}"

# Add PKCS11-HSE Library & Examples
IMAGE_INSTALL:append:s32cc = "${@bb.utils.contains('DISTRO_FEATURES', 'hse', ' pkcs11-hse', '', d)}"

# Add secboot public key
IMAGE_INSTALL:append:s32 = "${@bb.utils.contains('DISTRO_FEATURES', 'secboot', ' arm-trusted-firmware-secboot', '', d)}"
