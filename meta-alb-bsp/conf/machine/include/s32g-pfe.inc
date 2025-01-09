# Enable deployment of PFE firmware to 'sdcard' image
SDCARDIMAGE_BOOT_EXTRA_FILES:append = " pfe-firmware:s32g_pfe_class.fw"

# Enable deployment of PFE firmware to 'fsl-image-flash.flashimage'
FLASHIMAGE_EXTRA2 = "pfe-firmware"
FLASHIMAGE_EXTRA2_FILE = "s32g_pfe_class.fw"
FLASHIMAGE_EXTRA2_OFFSET = "${FLASHIMAGE_PFE_OFFSET}"
