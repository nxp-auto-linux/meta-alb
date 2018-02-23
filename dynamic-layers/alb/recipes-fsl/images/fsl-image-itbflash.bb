require recipes-core/images/core-image-minimal.bb

PACKAGE_ARCH = "${MACHINE_ARCH}"

#CORE_IMAGE_EXTRA_INSTALL += "udev-extraconf lsb"
CORE_IMAGE_EXTRA_INSTALL += "udev-extraconf"
CORE_IMAGE_EXTRA_INSTALL_append_qoriq = " udev-rules-qoriq"

IMAGE_FSTYPES = "tar.gz ext2.gz ext2.gz.u-boot"

SUMMARY = "Basic recovery image to be put into flash"
DESCRIPTION = "Small image which includes some helpful tools. \
It is meant for system recovery, rather than complex scenarios."

LICENSE = "MIT"

include recipes-fsl/images/fsl-image-core-common.inc

IMAGE_INSTALL_append = " \
    packagegroup-core-ssh-openssh \
"
#    packagegroup-fsl-tools-core \
#

IMAGE_INSTALL_append = " \
    memtester \
    minicom \
"

IMAGE_INSTALL_append_ls2 = " \
    restool \
"

inherit disable-services
ROOTFS_POSTPROCESS_COMMAND_append_ls1012a = "rootfs_disable_unnecessary_services;"

