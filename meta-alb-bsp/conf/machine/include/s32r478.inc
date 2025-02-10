require conf/machine/include/s32cc-board.inc

MACHINEOVERRIDES =. "s32r478:"

BOOT_TYPE:remove = "sdcard"
UBOOT_CONFIG:remove = "sdcard"

TFA_IMAGE_FILE = "fip.bin-qspi"
TFA_RESET_FILE = "bl2_w_dtb.s32-qspi"

SCMI_USE_SCMI_PINCTRL = "0"
SCMI_USE_SCMI_GPIO = "0"
SCMI_USE_SCMI_NVMEM = "0"
