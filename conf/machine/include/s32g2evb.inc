require conf/machine/include/s32g2.inc

MACHINEOVERRIDES =. "s32g2evb:"

UBOOT_DEFCONFIG = "s32g274aevb_defconfig"
UBOOT_FLASH_DEFCONFIG = "s32g274aevb_qspi_defconfig"

KERNEL_DEVICETREE ?= "freescale/fsl-s32g274a-evb.dtb"

FLASHIMAGE_EXTRA1_FILE = "fsl-s32g274a-evb.${FLASHIMAGE_DTB_SUFFIX}"
