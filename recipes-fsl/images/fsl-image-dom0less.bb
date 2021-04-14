# Copyright 2021 NXP

SUMMARY = "Image that generates a customizable rootfs to be used for Xen Dom0less VMs"

BASE_CORE_IMAGE ?= "recipes-core/images/core-image-minimal.bb"
include ${BASE_CORE_IMAGE}

IMAGE_FSTYPES = "cpio.gz"

IMAGE_INSTALL += " \
    kernel-image \
    kernel-modules \
    setserial \
    iputils \
    dhcpcd \
"
