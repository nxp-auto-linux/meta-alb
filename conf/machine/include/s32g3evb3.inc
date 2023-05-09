require conf/machine/include/s32g3.inc

MACHINEOVERRIDES =. "s32g3evb3:"
MACHINEOVERRIDES =. "s32gevbx:"

ATF_PLAT = "s32g3xxaevb3"

UBOOT_DEFCONFIG = "s32g3xxaevb3_defconfig"
UBOOT_FLASH_DEFCONFIG = "s32g3xxaevb3_qspi_defconfig"

KERNEL_DEVICETREE ??= "freescale/s32g3xxa-evb3.dtb"
require conf/machine/include/s32cc-pfems.inc

FLASHIMAGE_DTB_FILE = "s32g3xxa-evb3.${FLASHIMAGE_DTB_SUFFIX}"
