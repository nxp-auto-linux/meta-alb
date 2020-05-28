ATF_IMAGE = "arm-trusted-firmware"
ATF_IMAGE_FILE = "atf.s32"
UBOOT_NAME_SDCARD = "${ATF_IMAGE_FILE}"

# TODO remove these
#ATF_BL2_OFFSET = "0x34300000"
#ATF_BL31_OFFSET = "0x34200000"
#ATF_UBOOT_OFFSET = "0x34020000"

# Image offsets on the SD-card. Deployment in SRAM is controlled at runtime and
# via IVT.
ATF_BL1_MMC_OFFSET = "0x213000"
ATF_BL2_MMC_OFFSET = "0x1b2000"
ATF_BL31_MMC_OFFSET = "0x1f2000"
ATF_UBOOT_MMC_OFFSET = "0x2000"
