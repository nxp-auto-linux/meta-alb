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

# Our recovery image should have the smallest possible size.
# So we remove several things.
PACKAGES-CORE-benchmark = ""
PACKAGES-CORE_remove = "\
    gdbserver \
    lrzsz \
"
PACKAGES-CORE-MISC_remove = "\
    elfutils \
    pkgconfig \
    strongswan \
    tcpreplay \
    bridge-utils \
    inetutils-ftpd \
    inetutils-telnetd \
    inetutils-inetd \
    inetutils-rshd \
    inetutils-logger \
    inetutils-rsh \
    libhugetlbfs \
    lmsensors-sensors \
    tcpdump \
    tcpreplay \
    iptables \
"

# Given that it is a recover image, we may want to have some
# special tools.
IMAGE_INSTALL_append = " \
    memtester \
"

IMAGE_INSTALL_append_ls2 = " \
    restool \
"

IMAGE_INSTALL_append_lx2160a = " \
    restool \
"

