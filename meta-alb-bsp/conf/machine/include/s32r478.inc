require conf/machine/include/s32cc-board.inc

MACHINEOVERRIDES =. "s32r478:"

BOOT_TYPE:remove = "sdcard"
UBOOT_CONFIG:remove = "sdcard"

# Following BL2 info should be kept in sync with S32R478 DTS flashmap
TFA_IMAGE_FILE = "fip.bin-qspi"
TFA_RESET_FILE = "bl2_w_dtb.bin-qspi"

TFA_RESET_OFFSET ??= "0x1360000"
TFA_RESET_SIZE ??= "0xc0000"

SCMI_USE_SCMI_PINCTRL = "0"
SCMI_USE_SCMI_GPIO = "0"
SCMI_USE_SCMI_NVMEM = "0"
