SD_FAT_BOOT_DIR  = "BOOT/linux"
SD_FAT_DTB_DIR = "FIRMWARE/NXP/${TFA_PLAT}/linux"
SD_FAT_ENV_DIR = "FIRMWARE/NXP/${TFA_PLAT}/u-boot"

SD_INCLUDE_BL2_PART = "1"
SD_BL2_SIZE = "512"

SD_FIP_BURN_PART_INDEX = "2"
SD_FW_SIZE_ALIGNED = "${SD_BL2_SIZE_ALIGNED}"

SDCARD_PARTITIONING_SCHEME ?= "gpt"

BOOT_TYPE ?= "ufs sdcard"

SDCARD_BL2_PARTITION_NAME ?= "BL2"
SDCARD_FIP_PARTITION_NAME ?= "FIP"
SDCARD_FAT_PARTITION_NAME ?= "FAT"
SDCARD_ROOTFS_PARTITION_NAME ?= "linux-rootfs"

BASE_IMAGE_ROOTFS_ALIGNMENT ?= "1024"
IMAGE_ROOTFS_ALIGNMENT ?= "1024"

SDCARD_ROOTFS = "ext4"
UFS_ROOTFS = "ext4"
UBOOT_SUFFIX_SDCARD = "${UBOOT_SUFFIX}"

# U-Boot boots from the FIP as BL33, so the raw copy the sdcard class would write
# is unused duplication (and a large one would overrun the BL2 partition at 1 MiB).
# Empty name => nothing is written; UBOOT_BOOTSPACE_OFFSET only placed that copy,
# so it is dropped too.
UBOOT_NAME_SDCARD = ""

# remove the ".rootfs" that is added in the image output file names
IMAGE_NAME_SUFFIX = ""

TFA_IMAGE = "arm-trusted-firmware"
TFA_IMAGE_FILE ?= "fip.bin-sdcard"
TFA_ARTIFACTS_DIR ?= "arm-trusted-firmware"

BL2_IMAGE_FILE_SDCARD = "bl2.bin-sdcard"
BL2_NAME_SDCARD = "${BL2_IMAGE_FILE_SDCARD}"
SD_BL2_PATH = "${DEPLOY_DIR_IMAGE}/${BL2_NAME_SDCARD}"

FIP_IMAGE_FILE_SDCARD = "fip.bin-sdcard"
FIRMWARE_NAME_SDCARD = "${FIP_IMAGE_FILE_SDCARD}"
SD_FIP_PATH = "${DEPLOY_DIR_IMAGE}/${FIRMWARE_NAME_SDCARD}"

TFA_IMAGE_FILE_UFS ?= "fip.bin-ufs"

IMAGE_CMD:sdcard:append:s32n7sim () {
	# at the end, put the .sdcard artifact in the simimage folder
	cp -vf ${SDCARD} ${SDCARD_SIMIMAGE}
}

IMAGE_CMD:ufs:append:s32n7sim () {
	# at the end, put the UFS artifact in the simimage folder
	cp -vf ${UFS_LUN0_NAME} ${UFS_LUN0_SIMIMAGE}
	cp -vf ${UFS_LUN1_NAME} ${UFS_LUN1_SIMIMAGE}
}
