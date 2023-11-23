require recipes-core/images/core-image-minimal.bb

PACKAGE_ARCH = "${MACHINE_ARCH}"

IMAGE_INSTALL:append = " udev-extraconf lsb-release"

IMAGE_FSTYPES = "tar.gz cpio.gz"

SUMMARY = "Small image to be used for evaluating the Freescale socs"
DESCRIPTION = "Small image which includes some helpful tools and \
Freescale-specific packages. It is much more embedded-oriented \
than fsl-image-networking-full to evaluate the Freescale socs."

LICENSE = "MIT"

IMAGE_INSTALL:append = " \
    packagegroup-core-ssh-openssh \
    packagegroup-fsl-mfgtools \
    packagegroup-fsl-tools-core \
    packagegroup-fsl-benchmark-core \
    packagegroup-fsl-networking-core \
    packagegroup-fsl-tools-audio \
"

IMAGE_ROOTFS_DEP_EXT ??= "ext2.gz"

IMAGE_FSTYPES:append = " ${IMAGE_ROOTFS_DEP_EXT}"
