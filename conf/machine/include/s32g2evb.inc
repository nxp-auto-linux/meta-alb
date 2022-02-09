require conf/machine/include/s32g2.inc

MACHINEOVERRIDES =. "s32g2evb:"

ATF_PLAT = "s32g2xxaevb"

UBOOT_DEFCONFIG = "s32g2xxaevb_defconfig"
UBOOT_FLASH_DEFCONFIG = "s32g2xxaevb_qspi_defconfig"

KERNEL_DEVICETREE ?= "freescale/fsl-s32g2xxa-evb.dtb"

FLASHIMAGE_EXTRA1_FILE = "fsl-s32g2xxa-evb.${FLASHIMAGE_DTB_SUFFIX}"
