require conf/machine/include/s32cc-board.inc

KERNEL_DEFCONFIG ?= "s32cc_defconfig"

MACHINEOVERRIDES =. "s32g:"

UBOOT_CONFIG ?= "sdcard"
UBOOT_CONFIG[sdcard] = "${UBOOT_DEFCONFIG},,u-boot-nodtb.${UBOOT_SUFFIX}"

# prepend 'qspi' to keep the default deploy/u-boot-<MACHINE>.s32 to point to sdcard
UBOOT_CONFIG:prepend = "qspi "
UBOOT_CONFIG[qspi] = "${UBOOT_FLASH_DEFCONFIG},,u-boot-nodtb.${UBOOT_SUFFIX}"
