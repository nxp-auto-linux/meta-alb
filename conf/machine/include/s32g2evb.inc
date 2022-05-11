require conf/machine/include/s32g2.inc

MACHINEOVERRIDES =. "s32g2evb:"
MACHINEOVERRIDES =. "s32gevbx:"

ATF_PLAT = "s32g2xxaevb"

UBOOT_DEFCONFIG = "s32g2xxaevb_defconfig"
UBOOT_FLASH_DEFCONFIG = "s32g2xxaevb_qspi_defconfig"

KERNEL_DEVICETREE ?= "freescale/s32g2xxa-evb.dtb"

FLASHIMAGE_EXTRA1_FILE = "s32g2xxa-evb.${FLASHIMAGE_DTB_SUFFIX}"
