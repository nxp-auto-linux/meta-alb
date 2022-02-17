require conf/machine/include/s32-base.inc

SOC_FAMILY_append = ":gen1"

IMAGE_FSTYPES += "ext4"

# Specific definitions for .sdcard image generation
IMAGE_FSTYPES += "sdcard"
SDCARD_ROOTFS = "ext4"

UBOOT_SUFFIX_SDCARD = "${UBOOT_SUFFIX}"
IMAGE_CLASSES += "image_types_fsl_sdcard"

# Permit later to optionally add flashimage to IMAGE_FSTYPES
IMAGE_CLASSES += "image_types_fsl_flashimage"

UBOOT_ENV_SDCARD_OFFSET ?= "0x001e0000"

# Enable DM-Verity
require ${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'conf/machine/include/s32-gen1-dm-verity.inc', '', d)}

# When m7boot is enabled, the needed ATF settings are included from m7-boot.inc
require ${@bb.utils.contains('DISTRO_FEATURES', 'm7boot', 'conf/machine/include/m7-boot.inc', 'conf/machine/include/atf.inc', d)}
