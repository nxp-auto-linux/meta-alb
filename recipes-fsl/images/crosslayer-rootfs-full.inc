# This file switches between base root filesystems depending on
# meta-qoriq-demos layer being present or not.
# If present, use image-fsl-networking* as base for the filesystem
# as well as image for the virtualization part. Otherwise,
# use fsl-image-auto/base instead.

IMAGE_ROOTFS_REQ ?= "${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-qoriq", "fsl-image-networking-full", "fsl-image-auto", d)}"

# s32v234bbmini uses fsl-image-networking-full as IMAGE_ROOTFS_REQ and we must ensure
# the appropriate s32 content is brought in without getting duplicate inclusion warnings.
BASE_CORE_IMAGE:s32 = '${@oe.utils.conditional("IMAGE_ROOTFS_REQ", "fsl-image-auto", "recipes-core/images/core-image-minimal.bb", "", d)}'
EXTRA_REQUIRE:s32 = '${@oe.utils.conditional("IMAGE_ROOTFS_REQ", "fsl-image-auto", "", "fsl-image-base.bb fsl-image-s32-common.inc", d)}'

IMAGE_ROOTFS_DEP ?= "${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-qoriq", "fsl-image-networking", "fsl-image-base", d)}"
