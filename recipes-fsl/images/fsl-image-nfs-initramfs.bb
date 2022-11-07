# Copyright 2017-2018, 2022 NXP

SUMMARY = "Generate a minimal rootfs with a custom init script"

include recipes-core/images/core-image-minimal.bb

inherit image_types

require ${@bb.utils.contains('DISTRO_FEATURES', 'pfe', 'recipes-fsl/images/fsl-image-pfe.inc', '', d)}

IMAGE_INSTALL:append:s32g = "${@bb.utils.contains('DISTRO_FEATURES', 'llce-can', ' linux-firmware-llce-can', '', d)}"

IMAGE_INSTALL:append:s32g274abluebox3 = " init-net-root"

IMAGE_INSTALL:append += " \
	init-nfs-boot     \
	kernel-modules    \
	nfs-utils-client  \
"

# Remove unwanted images
rootfs_delete_Image () {
    find ${IMAGE_ROOTFS}/boot -name Image* | xargs rm -rf
}

ROOTFS_POSTPROCESS_COMMAND += "rootfs_delete_Image; "

IMAGE_FSTYPES:s32 = "cpio.gz.u-boot"

COMPATIBLE_MACHINE = "s32g274abluebox3"
