# This image is to be used in a flash only.

# We want to use the smaller flash targeted kernel as the normal
# kernel is getting too big for flashes
ITB_KERNEL ?= "linux-flash"
ITB_KERNEL_BASE_NAME ?= "kernel-flash"
FLASHIMAGE_KERNEL ?= "${ITB_KERNEL}"
KERNELDEPMODDEPEND = "${FLASHIMAGE_KERNEL}:do_packagedata"
KERNEL_DEPLOY_DEPEND = "${FLASHIMAGE_KERNEL}:do_deploy"

# To do that we need to ensure that we replace normal kernel module
# references with the smaller ones ...
PACKAGES-CORE:remove = "kernel-modules"
PACKAGES-CORE:append = "${ITB_KERNEL_BASE_NAME}-modules"
PACKAGES-CORE-MISC:remove = "kernel-modules"
PACKAGES-CORE-MISC:append = "${ITB_KERNEL_BASE_NAME}-modules"
POKY_DEFAULT_EXTRA_RRECOMMENDS:remove = "kernel-module-af-packet"
POKY_DEFAULT_EXTRA_RRECOMMENDS:append = "${ITB_KERNEL_BASE_NAME}-module-af-packet"
# ... and also ensure that no package pulls in the normal kernel
# even though we still reference the packagedata unfortunately
PACKAGES-CORE-MISC:remove = "iproute2-tc"
KERNELDEPMODDEPEND = "${ITB_KERNEL}:do_packagedata"
KERNEL_DEPLOY_DEPEND = "${ITB_KERNEL}:do_deploy"

require recipes-core/images/core-image-minimal.bb

PACKAGE_ARCH = "${MACHINE_ARCH}"

IMAGE_INSTALL:append = " udev-extraconf"

IMAGE_FSTYPES = "tar.gz ext2.gz ext2.gz.u-boot"

SUMMARY = "Basic recovery image to be put into flash"
DESCRIPTION = "Small image which includes some helpful tools. \
It is meant for system recovery, rather than complex scenarios."

LICENSE = "MIT"

include recipes-fsl/images/fsl-image-core-common.inc

# Our recovery image should have the smallest possible size.
# So we remove several things.
PACKAGES-CORE-benchmark = ""
PACKAGES-CORE:remove = "\
    gdbserver \
    lrzsz \
"
PACKAGES-CORE-MISC:remove = "\
    elfutils \
    pkgconfig \
    tcpreplay \
    bridge-utils \
    inetutils-ftpd \
    inetutils-telnetd \
    inetutils-inetd \
    inetutils-rshd \
    inetutils-logger \
    inetutils-rsh \
    lmsensors-sensors \
    tcpdump \
    tcpreplay \
    iptables \
"

# Given that it is a recover image, we may want to have some
# special tools.
IMAGE_INSTALL:append = " \
    memtester \
"

IMAGE_INSTALL:append:ls2 = " \
    restool \
"

IMAGE_INSTALL:append:lx2160a = " \
    restool \
"
