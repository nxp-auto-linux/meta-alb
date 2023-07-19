require conf/machine/include/s32-base.inc
require conf/machine/include/systemd.inc

SOC_FAMILY:append = ":s32cc"

IMAGE_FSTYPES += "ext4"

BOOT_TYPE ?= "sdcard qspi"

# Specific definitions for .sdcard image generation
IMAGE_FSTYPES += "sdcard"
SDCARD_ROOTFS = "ext4"

UBOOT_SUFFIX_SDCARD = "${UBOOT_SUFFIX}"

UBOOT_ENV_SDCARD_OFFSET ?= "0x00280000"

# The following variable enables dynamic retrieval of FLASHIMAGE offsets from the Linux's device tree
FLASHIMAGE_DYNAMIC_OFFSETS ?= "1"

# Enable DM-Verity
require ${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'conf/machine/include/s32cc-dm-verity.inc', '', d)}

# When m7boot is enabled, the needed ATF settings are included from m7-boot.inc
require ${@bb.utils.contains('DISTRO_FEATURES', 'm7boot', 'conf/machine/include/m7-boot.inc', 'conf/machine/include/atf.inc', d)}

# Check whether to switch from SIUL2 drivers to SCMI ones
SCMI_USE_SCMI_PINCTRL ??= "0"
SCMI_USE_SCMI_GPIO ??= "0"
SCMI_USE_SCMI_NVMEM ??= "0"
SCMI_DTB_NODE_CHANGE = "${@'true' if int(d.getVar('SCMI_USE_SCMI_PINCTRL')) or int(d.getVar('SCMI_USE_SCMI_GPIO')) or int(d.getVar('SCMI_USE_SCMI_NVMEM')) else 'false'}"

# Enable FIP Authentication. Please note that this will only generate an sdcard fip image.
require ${@bb.utils.contains('DISTRO_FEATURES', 'secboot', 'conf/machine/include/hse-secboot.inc', 'conf/machine/include/atf.inc', d)}

# Allow extensibility from other Yocto layers that define this include file
include conf/machine/include/s32cc-extra.inc
