require conf/machine/include/s32g3.inc

MACHINEOVERRIDES =. "s32g3evb:"

ATF_PLAT = "s32g3xxaevb"

UBOOT_DEFCONFIG = "s32g3xxaevb_defconfig"
UBOOT_FLASH_DEFCONFIG = "s32g3xxaevb_qspi_defconfig"

KERNEL_DEVICETREE ?= "freescale/fsl-s32g3xxa-evb.dtb"

FLASHIMAGE_EXTRA1_FILE = "fsl-s32g3xxa-evb.${FLASHIMAGE_DTB_SUFFIX}"
