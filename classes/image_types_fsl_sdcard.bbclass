inherit image_types_fsl

IMAGE_TYPES += "sdcard"

# Boot partition volume id
BOOTDD_VOLUME_ID ?= "boot_${MACHINE}"

