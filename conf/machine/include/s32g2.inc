require conf/machine/include/s32-gen1-board.inc

KERNEL_DEFCONFIG ?= "s32gen1_defconfig"

MACHINEOVERRIDES =. "s32g2:"

UBOOT_CONFIG ?= "sdcard"
UBOOT_CONFIG[sdcard] = "${UBOOT_DEFCONFIG},,u-boot.${UBOOT_SUFFIX}"

# prepend 'qspi' to keep the default deploy/u-boot-<MACHINE>.s32 to point to sdcard
UBOOT_CONFIG_prepend = "qspi "
UBOOT_CONFIG[qspi] = "${UBOOT_FLASH_DEFCONFIG},,u-boot.${UBOOT_SUFFIX}"

# Enable ATF
require ${@bb.utils.contains('DISTRO_FEATURES', 'atf', 'conf/machine/include/atf.inc', '', d)}

