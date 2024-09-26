inherit gpt_image_utils

IMAGE_TYPES += "ufs"

DEPENDS += "bc-native"

TFA_IMAGE_UFS ?= "arm-trusted-firmware"
TFA_IMAGE_FILE_UFS ?= "fip.bin-ufs"

UBOOT_ENV_UFS ?= "u-boot-environment"
UBOOT_ENV_NAME_UFS ?= "u-boot-flashenv-ufs"

UFS_LUN_DEPLOYDIR = "${IMGDEPLOYDIR}/simimage"
# set the names for the UFS LUN0 and LUN1 that will be produced
UFS_LUN0_NAME = "${UFS_LUN_DEPLOYDIR}/${IMAGE_BASENAME}-${MACHINE}.lun0.ufs"
UFS_LUN1_NAME = "${UFS_LUN_DEPLOYDIR}/${IMAGE_BASENAME}-${MACHINE}.lun1.ufs"

do_image_ufs[depends] += "mtools-native:do_populate_sysroot"
do_image_ufs[depends] += "parted-native:do_populate_sysroot"
do_image_ufs[depends] += "dosfstools-native:do_populate_sysroot"

do_image_ufs[depends] += "virtual/kernel:do_deploy"
do_image_ufs[depends] += "${IMAGE_BASENAME}:do_deploy"
do_image_ufs[depends] += " \
	${@d.getVar('UBOOT_ENV_NAME_UFS', True) and d.getVar('UBOOT_ENV_UFS', True) + ':do_deploy' or ''} \
	${@d.getVar('TFA_IMAGE_FILE_UFS', True) and d.getVar('TFA_IMAGE_UFS', True) + ':do_deploy' or ''} \
"

########################################################
# UFS LUN0 settings
########################################################

# BL2 partition
BL2_PARTITION_NAME = "BL2"
BL2_START_ADDRESS_SECTOR = "${LUN_FIRST_PARTITION_START_SECTOR}"
BL2_START_ADDRESS_KIB = "$(convert_sectors_to_kib "${BL2_START_ADDRESS_SECTOR}")"
BL2_ARTIFACT_LOCATION = "${DEPLOY_DIR_IMAGE}/simimage/arm-trusted-firmware/bl2_w_dtb.bin-ufs"
BL2_ARTIFACT_SIZE_KIB = "$(compute_artifact_size_kib ${BL2_ARTIFACT_LOCATION})"
# we want the BL2 partition to be of fixed size (512K)
BL2_PARTITION_SIZE_KIB ?= "512"
BL2_PARTITION_SIZE_SECTORS = "$(convert_kib_to_sectors "${BL2_PARTITION_SIZE_KIB}")"

# FIP partition
FIP_PARTITION_NAME = "FIP"
FIP_START_ADDRESS_SECTOR = "$(compute_next_aligned_gpt_partition_start_sector ${BL2_START_ADDRESS_SECTOR} ${BL2_PARTITION_SIZE_SECTORS})"
FIP_START_ADDRESS_KIB = "$(convert_sectors_to_kib ${FIP_START_ADDRESS_SECTOR})"
FIP_ARTIFACT_LOCATION = "${DEPLOY_DIR_IMAGE}/simimage/arm-trusted-firmware/${TFA_IMAGE_FILE_UFS}"
FIP_ARTIFACT_SIZE_KIB = "$(compute_artifact_size_kib ${FIP_ARTIFACT_LOCATION})"
# we want the FIP partition to be of fixed size (1M)
FIP_PARTITION_SIZE_KIB ?= "1024"
FIP_PARTITION_SIZE_SECTORS = "$(convert_kib_to_sectors ${FIP_PARTITION_SIZE_KIB})"

# FIRMWARES partition
FIRMWARES_PARTITION_NAME = "firmwares"
FIRMWARES_PARTITION_START_ADDRESS_SECTOR = "$(compute_next_aligned_gpt_partition_start_sector ${FIP_START_ADDRESS_SECTOR} ${FIP_PARTITION_SIZE_SECTORS})"
FIRMWARES_PARTITION_START_ADDRESS_KIB = "$(convert_sectors_to_kib ${FIRMWARES_PARTITION_START_ADDRESS_SECTOR})"
FIRMWARES_FOLDER ?= "/FIRMWARE"
FIRMWARES_FOLDER_VENDOR ?= "${FIRMWARES_FOLDER}/NXP"
KERNEL_DEVICETREE_BASENAME = "$(basename ${KERNEL_DEVICETREE})"
KERNEL_DEVICETREE_ARTIFACT = "${DEPLOY_DIR_IMAGE}/simimage/kernel/${KERNEL_DEVICETREE_BASENAME}"
KERNEL_DEVICETREE_SIZE_KIB = "$(compute_artifact_size_kib ${KERNEL_DEVICETREE_ARTIFACT})"
UBOOT_ENV_SIZE_KIB ?= "16"
UBOOT_ENV_UFS_FILE ?= "${DEPLOY_DIR_IMAGE}/${@d.getVar('UBOOT_ENV_NAME_UFS', True) and (d.getVar('UBOOT_ENV_NAME_UFS', True) + '-${MACHINE}.bin') or ''}"
FIRMWARES_PARTITION_EXTRA_SPACE_KIB ?= "256"
FIRMWARES_PARTITION_SIZE_KIB = "$(echo "${KERNEL_DEVICETREE_SIZE_KIB} + ${UBOOT_ENV_SIZE_KIB} + ${FIRMWARES_PARTITION_EXTRA_SPACE_KIB}" | bc)"
FIRMWARES_PARTITION_SIZE_SECTORS = "$(convert_kib_to_sectors ${FIRMWARES_PARTITION_SIZE_KIB})"

generate_nxp_ufs_lun0 () {

	# perform sanity checks for LUN0
	if [ "${BL2_ARTIFACT_SIZE_KIB}" -gt "${BL2_PARTITION_SIZE_KIB}" ]; then
		bbfatal "BL2 artifact size is greater than the partition size! Please increase the size of the BL2 partition (BL2_PARTITION_SIZE_KIB) in your local.conf file!"
	fi

	if [ "${FIP_ARTIFACT_SIZE_KIB}" -gt "${FIP_PARTITION_SIZE_KIB}" ]; then
		bbfatal "FIP artifact size is greater than the partition size! Please increase the size of the FIP partition (FIP_PARTITION_SIZE_KIB) in your local.conf file!"
	fi

	BL2_ROUND_SIZE_KIB=$(roundup_size_kib ${BL2_PARTITION_SIZE_KIB} ${OPTIMAL_ALIGNMENT_KIB})
	FIP_ROUND_SIZE_KIB=$(roundup_size_kib ${FIP_PARTITION_SIZE_KIB} ${OPTIMAL_ALIGNMENT_KIB})
	FIRMWARES_ROUND_SIZE_KIB=$(roundup_size_kib ${FIRMWARES_PARTITION_SIZE_KIB} ${OPTIMAL_ALIGNMENT_KIB})

	# when computing the total size of the LUN, we should consider that after the end of the last 
	# partition, there is also the alternate GPT header + table entries
	LUN0_TOTAL_SIZE=$(echo "${LUN_FIRST_PARTITION_START_KIB} + ${BL2_ROUND_SIZE_KIB} + ${FIP_ROUND_SIZE_KIB} + ${FIRMWARES_ROUND_SIZE_KIB} + ${GPT_SIZE_KIB}" | bc)

	dd if=/dev/zero of="${UFS_LUN0_NAME}" bs=1K count="${LUN0_TOTAL_SIZE}"

	# Create partition table for LUN0
	export PARTED_SECTOR_SIZE=${SECTOR_SIZE}
	parted -s "${UFS_LUN0_NAME}" mklabel gpt

	create_partition ${UFS_LUN0_NAME} ${BL2_PARTITION_NAME} ${BL2_PARTITION_SIZE_SECTORS}
	create_partition ${UFS_LUN0_NAME} ${FIP_PARTITION_NAME} ${FIP_PARTITION_SIZE_SECTORS}
	create_partition ${UFS_LUN0_NAME} ${FIRMWARES_PARTITION_NAME} ${FIRMWARES_PARTITION_SIZE_SECTORS} "fat16"

	write_partition ${BL2_ARTIFACT_LOCATION} ${UFS_LUN0_NAME} ${BL2_ARTIFACT_SIZE_KIB} ${BL2_START_ADDRESS_KIB}
	write_partition ${FIP_ARTIFACT_LOCATION} ${UFS_LUN0_NAME} ${FIP_ARTIFACT_SIZE_KIB} ${FIP_START_ADDRESS_KIB}

	# create FAT filesystem
	rm -f "${UFS_LUN_DEPLOYDIR}/${FIRMWARES_PARTITION_NAME}.part"
	mkfs.vfat -n ${FIRMWARES_PARTITION_NAME} -S ${SECTOR_SIZE} -C "${UFS_LUN_DEPLOYDIR}/${FIRMWARES_PARTITION_NAME}.part" ${FIRMWARES_PARTITION_SIZE_KIB}
	mmd -i "${UFS_LUN_DEPLOYDIR}/${FIRMWARES_PARTITION_NAME}.part" "::${FIRMWARES_FOLDER}" "::${FIRMWARES_FOLDER_VENDOR}" "::${FIRMWARES_FOLDER_VENDOR}/${MACHINE}" "::${FIRMWARES_FOLDER_VENDOR}/${MACHINE}/linux" "::${FIRMWARES_FOLDER_VENDOR}/${MACHINE}/u-boot"
	mcopy -i "${UFS_LUN_DEPLOYDIR}/${FIRMWARES_PARTITION_NAME}.part" -s "${KERNEL_DEVICETREE_ARTIFACT}" "::${FIRMWARES_FOLDER_VENDOR}/${MACHINE}/linux/${KERNEL_DEVICETREE_BASENAME}"
	mcopy -i "${UFS_LUN_DEPLOYDIR}/${FIRMWARES_PARTITION_NAME}.part" -s "${UBOOT_ENV_UFS_FILE}" "::${FIRMWARES_FOLDER_VENDOR}/${MACHINE}/u-boot/env.txt"

	write_partition "${UFS_LUN_DEPLOYDIR}/${FIRMWARES_PARTITION_NAME}.part" "${UFS_LUN0_NAME}" "${FIRMWARES_PARTITION_SIZE_KIB}" "${FIRMWARES_PARTITION_START_ADDRESS_KIB}"
	rm -f "${UFS_LUN_DEPLOYDIR}/${FIRMWARES_PARTITION_NAME}.part"
}


########################################################
# UFS LUN1 settings
########################################################

# ESP partition
ESP_PARTITION_NAME = "EFI-System-Partition"
ESP_PARTITION_START_ADDRESS_SECTOR = "${LUN_FIRST_PARTITION_START_SECTOR}"
ESP_PARTITION_START_ADDRESS_KIB = "$(convert_sectors_to_kib ${ESP_PARTITION_START_ADDRESS_SECTOR})"
ESP_ARTIFACT_NAME = "${DEPLOY_DIR_IMAGE}/simimage/kernel/${KERNEL_IMAGETYPE}"
ESP_ARTIFACT_SIZE_KIB = "$(compute_artifact_size_kib "${ESP_ARTIFACT_NAME}")"
ESP_PARTITION_EXTRA_SPACE_MIB ?= "2"
ESP_PARTITION_SIZE_KIB = "$(echo "${ESP_ARTIFACT_SIZE_KIB} + ${ESP_PARTITION_EXTRA_SPACE_MIB} * 1024" | bc)"
ESP_PARTITION_SIZE_SECTORS = "$(convert_kib_to_sectors ${ESP_PARTITION_SIZE_KIB})"


# ROOTFS partition
ROOTFS_PARTITION_NAME = "linux-rootfs"
ROOTFS_PARTITION_START_ADDRESS_SECTOR = "$(compute_next_aligned_gpt_partition_start_sector ${ESP_PARTITION_START_ADDRESS_SECTOR} ${ESP_PARTITION_SIZE_SECTORS})"
ROOTFS_PARTITION_START_ADDRESS = "$(convert_sectors_to_kib ${ROOTFS_PARTITION_START_ADDRESS_SECTOR})"
ROOTFS_EXT4_ARTIFACT_NAME = "${DEPLOY_DIR_IMAGE}/ufsimage/${IMAGE_BASENAME}-${MACHINE}.ext4"
ROOTFS_PARTITION_SIZE_KIB = "$(compute_artifact_size_kib "${ROOTFS_EXT4_ARTIFACT_NAME}")"
ROOTFS_PARTITION_SIZE_SECTORS = "$(convert_kib_to_sectors "${ROOTFS_PARTITION_SIZE_KIB}")"

generate_nxp_ufs_lun1 () {

	ESP_PARTITION_ROUND_SIZE_KIB=$(roundup_size_kib ${ESP_PARTITION_SIZE_KIB} ${OPTIMAL_ALIGNMENT_KIB})
	ROOTFS_PARTITION_ROUND_SIZE_KIB=$(roundup_size_kib ${ROOTFS_PARTITION_SIZE_KIB} ${OPTIMAL_ALIGNMENT_KIB})

	# when computing the total size of the LUN, we should consider that after the end of the last 
	# partition, there is also the alternate GPT header + partition entry array
	LUN1_TOTAL_SIZE=$(echo "${LUN_FIRST_PARTITION_START_KIB} + ${ESP_PARTITION_ROUND_SIZE_KIB} + ${ROOTFS_PARTITION_ROUND_SIZE_KIB} + ${GPT_SIZE_KIB}" | bc)

	dd if=/dev/zero of="${UFS_LUN1_NAME}" bs=1K count=${LUN1_TOTAL_SIZE}

	export PARTED_SECTOR_SIZE=${SECTOR_SIZE}
	parted -s "${UFS_LUN1_NAME}" mklabel gpt

	create_partition ${UFS_LUN1_NAME} ${ESP_PARTITION_NAME} ${ESP_PARTITION_SIZE_SECTORS} "fat16"
	parted -s "${UFS_LUN1_NAME}" set 1 esp on
	create_partition ${UFS_LUN1_NAME} ${ROOTFS_PARTITION_NAME} ${ROOTFS_PARTITION_SIZE_SECTORS} "ext4"

	# prepare the linux kernel partition
	rm -f "${UFS_LUN_DEPLOYDIR}/kernel.part"
	mkfs.vfat -n boot -S ${SECTOR_SIZE} -C "${UFS_LUN_DEPLOYDIR}/kernel.part" ${ESP_PARTITION_SIZE_KIB}
	mcopy -i "${UFS_LUN_DEPLOYDIR}/kernel.part" -s "${ESP_ARTIFACT_NAME}" "::/${KERNEL_IMAGETYPE}"

	write_partition "${UFS_LUN_DEPLOYDIR}/kernel.part" ${UFS_LUN1_NAME} ${ESP_PARTITION_SIZE_KIB} ${ESP_PARTITION_START_ADDRESS_KIB}
	write_partition ${ROOTFS_EXT4_ARTIFACT_NAME} ${UFS_LUN1_NAME} ${ROOTFS_PARTITION_SIZE_KIB} ${ROOTFS_PARTITION_START_ADDRESS}

	rm -f "${UFS_LUN_DEPLOYDIR}/kernel.part"
}

generate_nxp_ufs () {
	mkdir -p "${UFS_LUN_DEPLOYDIR}"
	generate_nxp_ufs_lun0
	generate_nxp_ufs_lun1
}

IMAGE_CMD:ufs () {
	cd "${IMGDEPLOYDIR}"
	generate_nxp_ufs
	cd -
}
