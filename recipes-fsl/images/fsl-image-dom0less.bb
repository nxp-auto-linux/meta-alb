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
# Add getty spawn on ttyAMA0 in Dom0less DomUs' rootfs
fix_inittab() {
    INITTAB_AMA0="A0:12345:respawn:/bin/start_getty 115200 ttyAMA0"
    echo "${INITTAB_AMA0}" >> ${IMAGE_ROOTFS}${sysconfdir}/inittab
}

ROOTFS_POSTPROCESS_COMMAND += "fix_inittab; "

# Fix do_package warning when depending on this recipe
deltask do_packagedata