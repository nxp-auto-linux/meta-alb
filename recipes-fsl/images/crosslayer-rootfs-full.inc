# This file switches between base root filesystems depending on
# meta-qoriq-demos layer being present or not.
# If present, use image-fsl-networking* as base for the filesystem
# as well as image for the virtualization part. Otherwise,
# use fsl-image-auto/base instead.

IMAGE_ROOTFS_REQ ?= "${@bb.utils.contains("BBFILE_COLLECTIONS", "qoriq-demos", "fsl-image-networking-full", "fsl-image-auto", d)}"
EXTRA_REQUIRE_s32 = "${@bb.utils.contains("BBFILE_COLLECTIONS", "qoriq-demos", "fsl-image-s32-common.inc", "", d)}"

IMAGE_ROOTFS_DEP ?= "${@bb.utils.contains("BBFILE_COLLECTIONS", "qoriq-demos", "fsl-image-networking", "fsl-image-base", d)}"

IMAGE_ROOTFS_DEP_DEFAULT_EXT ?= "ext3"
IMAGE_ROOTFS_DEP_DEFAULT_EXT_s32 = "cpio.gz"

IMAGE_ROOTFS_DEP_EXT ?= "${@bb.utils.contains("BBFILE_COLLECTIONS", "qoriq-demos", "ext2.gz", "${IMAGE_ROOTFS_DEP_DEFAULT_EXT}", d)}"
