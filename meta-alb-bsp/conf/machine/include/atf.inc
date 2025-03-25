# Copyright 2020-2021, 2024 NXP

TFA_IMAGE = "arm-trusted-firmware"
TFA_IMAGE_FILE ?= "fip.bin-sdcard"
TFA_RESET = "arm-trusted-firmware"
TFA_RESET_FILE ?= "bl2_w_dtb.s32-sdcard"
UBOOT_NAME_SDCARD = "${TFA_RESET_FILE}"
FIRMWARE_NAME_SDCARD = "${TFA_IMAGE_FILE}"

FLASHIMAGE_FIP = "arm-trusted-firmware"
FLASHIMAGE_FIP_FILE = "fip.bin-qspi"
FLASHIMAGE_RESET = "arm-trusted-firmware"
FLASHIMAGE_RESET_FILE ?= "bl2_w_dtb.s32-qspi"
