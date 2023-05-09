require conf/machine/include/s32g3.inc

MACHINEOVERRIDES =. "s32g3evb:"
MACHINEOVERRIDES =. "s32gevbx:"

ATF_PLAT = "s32g3xxaevb"

UBOOT_DEFCONFIG = "s32g3xxaevb_defconfig"
UBOOT_FLASH_DEFCONFIG = "s32g3xxaevb_qspi_defconfig"

KERNEL_DEVICETREE ??= "freescale/s32g3xxa-evb.dtb"
require conf/machine/include/s32cc-pfems.inc

FLASHIMAGE_DTB_FILE = "s32g3xxa-evb.${FLASHIMAGE_DTB_SUFFIX}"
