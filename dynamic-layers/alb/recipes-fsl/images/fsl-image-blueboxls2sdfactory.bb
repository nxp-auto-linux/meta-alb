# This image is useful only for the BlueBox Mini.
# This BlueBox is capable of directly booting the LS2 from SDHC,
# which permits nearly fully automatic factory imaging.

SDCARD_ROOTFS_IMAGE = "fsl-image-auto"

require recipes-fsl/images/${SDCARD_ROOTFS_IMAGE}.bb

# We need the Aquantia firmware to properly image a BlueBox Mini
# and we want phytool to be able to debug boards on bring up
# FIX! POSSIBLE LEGAL ISSUE ABOUT FIRMWARE IMAGE DISTRIBUTION IN YOCTO?!
IMAGE_INSTALL_append_ls2084abbmini += "\
    aqr-firmware-image \
"

IMAGE_INSTALL += "\
    phytool \
"

# The factory image should include the full NOR image for
# convenience. Technically, the initial SD card space contains
# all elements to flash the NOR properly, but the point here
# is that we use the exact image to enable flashing NOR.
inherit fsl-rootfsimage
do_rootfs[depends] += "fsl-image-blueboxbootflash:do_image_complete ${SDCARD_ROOTFS_IMAGE}:do_image_complete bbdeployscripts:do_deploy"
IMAGE_ROOTFS_IMAGELIST = "fsl-image-blueboxbootflash-${MACHINE}.flashimage ${SDCARD_ROOTFS_IMAGE}-${MACHINE}.tar.gz bbdeployimage.itb bbdeployimage.sh bbreplacerootfs.sh"

# Generating an SDHC image to be directly booted with RCW=0x40
IMAGE_FSTYPES_append = " sdcard"
SDCARD_ROOTFS = "${IMGDEPLOYDIR}/${IMAGE_NAME}.rootfs.ext3"
SDCARD_RCW_ls2084abbmini = "rcw"
SDCARD_RCW_NAME_ls2084abbmini = "rcw/${MACHINE}/FFFFPPHH_0x2a_0x41/rcw_ffffpphh_0x2a_0x41_SDHC_1800_700_1867_1600.bin"
BOOT_SPACE = "65536"
SDCARD_BINARY_SPACE = "65536"
UBOOT_BOOTSPACE_OFFSET  = "${FLASHIMAGE_UBOOT_OFFSET}"

# For factory imaging, we use a custom U-Boot environment
SDCARDIMAGE_EXTRA4_FILE_ls2084abbmini = "u-boot-flashenv-factory-${MACHINE}.bin"

# For the factory reimaging SD card, we also add the AQR firmware
# setup into the flash area for BlueBox Mini
SDCARDIMAGE_EXTRA8_ls2084abbmini = "aqr-firmware"
SDCARDIMAGE_EXTRA8_FILE_ls2084abbmini = "AQR-G2_v3.3.A-AQR_Freescale_AQR107_ID16066_VER554.cld"
SDCARDIMAGE_EXTRA8_OFFSET_ls2084abbmini = "0x00900000"
SDCARDIMAGE_EXTRA9_ls2084abbmini = "aqr-firmware"
SDCARDIMAGE_EXTRA9_FILE_ls2084abbmini = "aq_programming.bin"
SDCARDIMAGE_EXTRA9_OFFSET_ls2084abbmini = "0x00980000"

COMPATIBLE_MACHINE = "ls2084abbmini"
