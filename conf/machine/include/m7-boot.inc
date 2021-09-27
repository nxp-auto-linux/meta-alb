# Copyright 2021 NXP

IMAGE_BOOTLOADER_RECIPE = "m7-sample"
FLASHIMAGE_UBOOT = "m7-sample"

UBOOT_NAME_SDCARD = "${@bb.utils.contains('DISTRO_FEATURES', 'atf', \
	    'fip.s32-sdcard.m7',\
	    'u-boot-${MACHINE}.s32-sdcard.m7',\
	    d)}"

FLASHIMAGE_UBOOT_FILE = "${@bb.utils.contains('DISTRO_FEATURES', 'atf', \
	    'fip.s32-qspi.m7',\
	    '${FLASHIMAGE_UBOOT_BASENAME}-${MACHINE}${FLASHIMAGE_UBOOT_REALSUFFIX}${@oe.utils.conditional("FLASHIMAGE_UBOOT_TYPE", "", "", "-${FLASHIMAGE_UBOOT_TYPE}", d)}.m7',\
	    d)}"

ATF_IMAGE = "${@bb.utils.contains('DISTRO_FEATURES', 'atf', \
	    'fip.s32-qspi.m7',\
	    '',\
	    d)}"
