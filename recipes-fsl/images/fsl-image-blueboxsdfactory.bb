# This image is useful only for the BlueBox Mini and BlueBox 3.
# This BlueBox is capable of directly booting the LS2 from SDHC,
# which permits nearly fully automatic factory imaging.

FACTORY_SDCARD_ROOTFS_IMAGE ??= "fsl-image-auto"
FACTORY_SDCARD_ROOTFS_IMAGE_ubuntu ?= "fsl-image-ubuntu"

require recipes-fsl/images/${FACTORY_SDCARD_ROOTFS_IMAGE}.bb

# We need the Aquantia firmware to properly image a BlueBox Mini
# and we want phytool to be able to debug boards on bring up
# The Aquantia binaries (bin and cld) must be downloaded
# separately.
IMAGE_INSTALL_append_ls2084abbmini += "${@bb.utils.contains('DISTRO_FEATURES', 'aqr', 'aqr-firmware-image', '', d)}"

FACTORY_EXTRA_IMAGE_INSTALL = "\
    phytool \
"
FACTORY_EXTRA_IMAGE_INSTALL_ubuntu = ""

IMAGE_INSTALL += "${FACTORY_EXTRA_IMAGE_INSTALL}"

# The factory image should include the full NOR image for
# convenience. Technically, the initial SD card space contains
# all elements to flash the NOR properly, but the point here
# is that we use the exact image to enable flashing NOR.
inherit fsl-rootfsimage
FACTORY_FLASH_IMAGE_NAME ?= "fsl-image-flash"
FACTORY_FLASH_IMAGE ?= "${FACTORY_FLASH_IMAGE_NAME}-${MACHINE}.flashimage"
# no flash image yet for ubuntu
FACTORY_FLASH_IMAGE_NAME_ubuntu = "${FACTORY_SDCARD_ROOTFS_IMAGE}"
FACTORY_FLASH_IMAGE_ubuntu = ""
do_rootfs[depends] += "${FACTORY_FLASH_IMAGE_NAME}:do_image_complete ${FACTORY_SDCARD_ROOTFS_IMAGE}:do_image_complete bbdeployscripts:do_deploy"
IMAGE_ROOTFS_IMAGELIST_ls2084abbmini = "${FACTORY_FLASH_IMAGE} ${FACTORY_SDCARD_ROOTFS_IMAGE}-${MACHINE}.tar.gz bbdeployimage.itb bbdeployimage.sh bbreplacerootfs.sh"
IMAGE_ROOTFS_IMAGELIST_s32g274abluebox3 = "${FACTORY_FLASH_IMAGE} ${FACTORY_SDCARD_ROOTFS_IMAGE}-${MACHINE}.sdcard bbdeployimage.itb"

# For factory imaging, we use a custom U-Boot environment
SDCARDIMAGE_EXTRA4_FILE_ls2084abbmini = "u-boot-flashenv-factory-${MACHINE}.bin"

# For the factory reimaging SD card, we also add the AQR firmware
# setup into the flash area for BlueBox Mini
SDCARDIMAGE_EXTRA8_ls2084abbmini = "${@bb.utils.contains('DISTRO_FEATURES', 'aqr', 'aqr-firmware', '', d)}"
SDCARDIMAGE_EXTRA8_FILE_ls2084abbmini = "${@bb.utils.contains('DISTRO_FEATURES', 'aqr', 'AQR-G2_v3.3.A-AQR_Freescale_AQR107_ID16066_VER554.cld', '', d)}"
SDCARDIMAGE_EXTRA8_OFFSET_ls2084abbmini = "0x00900000"
SDCARDIMAGE_EXTRA9_ls2084abbmini = "${@bb.utils.contains('DISTRO_FEATURES', 'aqr', 'aqr-firmware', '', d)}"
SDCARDIMAGE_EXTRA9_FILE_ls2084abbmini = "${@bb.utils.contains('DISTRO_FEATURES', 'aqr', 'aq_programming.bin', '', d)}"
SDCARDIMAGE_EXTRA9_OFFSET_ls2084abbmini = "0x00980000"

COMPATIBLE_MACHINE = "ls2084abbmini|s32g274abluebox3"
