require conf/machine/include/ubuntubasics.inc

FLASHIMAGE_ROOTFS = "fsl-image-ubuntu-base"

# Temporary removal of flash image filesystem support
# for Ubuntu images due to an issue regarding 'cpio'
# utilitary on Ubuntu 18.04 host when seeking on
# large files (> 2 GB)
IMAGE_CLASSES_remove += "image_types_fsl_flashimage"
IMAGE_FSTYPES_remove += "flashimage"
IMAGE_TYPES_MASKED += "cpio cpio.gz"

BOOT_SCRIPTS = ""
DISTRO_FEATURES_append += "docker"

# Temporary removal of flash image filesystem support
# for Ubuntu images due to an issue regarding 'cpio'
# utilitary on Ubuntu 18.04 host when seeking on
# large files (> 2 GB)
IMAGE_FSTYPES_remove = "${FLASHIMAGE_ROOTFS_SUFFIX}"
