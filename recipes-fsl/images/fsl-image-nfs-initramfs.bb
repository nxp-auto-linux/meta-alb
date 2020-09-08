# Copyright 2017-2018 NXP

SUMMARY = "Generate a minimal rootfs with a custom init script"

include recipes-core/images/core-image-minimal.bb

inherit image_types

# Support for SJA1105 swich under Linux
IMAGE_INSTALL_append_s32v234evb = " sja1105 "
IMAGE_INSTALL_append_s32v234bbmini = " sja1105 "
IMAGE_INSTALL_append_s32v234campp = " init-net-root"

IMAGE_INSTALL_append += " \
	init-nfs-boot     \
	kernel-modules    \
	nfs-utils         \
"

# Remove unwanted images
rootfs_delete_Image () {
    find ${IMAGE_ROOTFS}/boot -name Image* | xargs rm -rf
}

ROOTFS_POSTPROCESS_COMMAND += "rootfs_delete_Image; "

IMAGE_FSTYPES_s32v2xx = "cpio.gz.u-boot"

COMPATIBLE_MACHINE = "s32v234evb|s32v234bbmini|s32v234campp"
