# Copyright 2023 NXP

ATF_IMAGE = "arm-trusted-firmware"
ATF_IMAGE_FILE = "fip.s32-sdcard"
UBOOT_NAME_SDCARD = "${ATF_IMAGE_FILE}"

SDCARDIMAGE_BOOT_EXTRA_FILES:append = " arm-trusted-firmware:fip_rsa2048_public.pem"

DISTRO_FEATURES:append = " hse"
