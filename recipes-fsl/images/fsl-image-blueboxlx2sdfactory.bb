# This image is useful only for the LX2160ABlueBox3.
# This BlueBox is capable of directly booting the LX2 from SDHC,
# which permits nearly fully automatic factory imaging.

FACTORY_SDCARD_ROOTFS_IMAGE ??= "fsl-image-auto"
FACTORY_SDCARD_ROOTFS_IMAGE_ubuntu ?= "fsl-image-ubuntu"

require recipes-fsl/images/${FACTORY_SDCARD_ROOTFS_IMAGE}.bb

# The factory image should include the full NOR image for
# convenience. Technically, the initial SD card space contains
# all elements to flash the NOR properly, but the point here
# is that we use the exact image to enable flashing NOR.
inherit fsl-rootfsimage
FACTORY_FLASH_IMAGE_NAME ?= "fsl-image-flash"
FACTORY_FLASH_IMAGE_NAME_ubuntu = "${FACTORY_SDCARD_ROOTFS_IMAGE}"
FACTORY_FLASH_IMAGE ?= "${FACTORY_FLASH_IMAGE_NAME}-${MACHINE}.flashimage"
# no flash image yet for ubuntu
FACTORY_FLASH_IMAGE_NAME_ubuntu = "${FACTORY_SDCARD_ROOTFS_IMAGE}"
FACTORY_FLASH_IMAGE_ubuntu = ""
do_rootfs[depends] += "${FACTORY_FLASH_IMAGE_NAME}:do_image_complete ${FACTORY_SDCARD_ROOTFS_IMAGE}:do_image_complete"

IMAGE_ROOTFS_IMAGELIST = "${FACTORY_FLASH_IMAGE} ${FACTORY_SDCARD_ROOTFS_IMAGE}-${MACHINE}.tar.gz"

COMPATIBLE_MACHINE = "lx2160abluebox3"
