ATF_IMAGE = "arm-trusted-firmware"
ATF_IMAGE_FILE = "atf.s32"
UBOOT_NAME_SDCARD = "${ATF_IMAGE_FILE}"

# Addresses in SRAM
ATF_BL2_LOADADDR = "0x34300000"
ATF_BL2_ENTRYPOINT = "0x34302000"
ATF_UBOOT_ENTRYPOINT = "0x34020000"

# Image offsets on the SD-card. Deployment in SRAM is controlled at runtime and
# via IVT.
ATF_BL2_MMC_OFFSET = "1b2000"
ATF_BL31_MMC_OFFSET = "1f2000"
ATF_UBOOT_MMC_OFFSET = "2000"

# ATF uses its own dtb files
ATF_DTB_FILE_s32g274aevb = "fsl-s32g274a-atf-evb.dtb"
ATF_DTB_FILE_s32g274ardb = "fsl-s32g274a-atf-rdb.dtb"
