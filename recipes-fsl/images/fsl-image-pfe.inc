
# Enable deployment of PFE firmware to 'sdcard' image
SDCARDIMAGE_BOOT_EXTRA2 = "pfe"
SDCARDIMAGE_BOOT_EXTRA2_FILE = "s32g_pfe_class.fw"

# Enable deployment of PFE firmware to 'fsl-image-flash.flashimage'
FLASHIMAGE_EXTRA2 = "pfe"
FLASHIMAGE_EXTRA2_FILE = "s32g_pfe_class.fw"
FLASHIMAGE_EXTRA2_OFFSET = "${FLASHIMAGE_PFE_OFFSET}"

IMAGE_INSTALL_append_s32g2 += " pfe libfci-cli"
