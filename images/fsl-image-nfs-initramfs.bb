# Copyright 2017 NXP

SUMMARY = "Generate a minimal rootfs with a custom init script"

include recipes-core/images/core-image-minimal.bb

inherit image_types_uboot

# Support for SJA1105 swich under Linux
IMAGE_INSTALL_append_s32v234evb = " sja1105"

IMAGE_INSTALL_append += " \
	init-nfs-boot     \
	nfs-utils         \
"

IMAGE_FSTYPES_s32v2xx = "cpio.gz.u-boot"
