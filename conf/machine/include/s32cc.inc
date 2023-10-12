require conf/machine/include/s32-base.inc

SOC_FAMILY:append = ":s32cc"

INIT_MANAGER ?= "systemd"

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
# Enable FIP Authentication
require ${@bb.utils.contains('DISTRO_FEATURES', 'secboot', 'conf/machine/include/hse-secboot.inc', '', d)}

# Check whether to switch from SIUL2 drivers to SCMI ones
SCMI_USE_SCMI_PINCTRL ??= "${@bb.utils.contains('DISTRO_FEATURES', 'scprt', '1', '0', d)}"
SCMI_USE_SCMI_GPIO ??= "${@bb.utils.contains('DISTRO_FEATURES', 'scprt', '1', '0', d)}"
SCMI_USE_SCMI_NVMEM ??= "0"
SCMI_DTB_NODE_CHANGE = "${@'true' if int(d.getVar('SCMI_USE_SCMI_PINCTRL')) or int(d.getVar('SCMI_USE_SCMI_GPIO')) or int(d.getVar('SCMI_USE_SCMI_NVMEM')) else 'false'}"

# Allow extensibility from other Yocto layers that define this include file
include conf/machine/include/s32cc-extra.inc
