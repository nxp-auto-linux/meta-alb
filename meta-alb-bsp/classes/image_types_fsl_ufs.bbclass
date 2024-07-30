inherit image_types_fsl
inherit rawimageutils


IMAGE_TYPES += "ufs"

DEPENDS += "bc-native"

TFA_IMAGE_UFS ?= "arm-trusted-firmware"
TFA_IMAGE_FILE_UFS = "fip.bin"

UFS_LUN_DEPLOYDIR = "${IMGDEPLOYDIR}/simimage"
# set the names for the UFS LUN0 and LUN1 that will be produced
UFS_LUN0_NAME = "${UFS_LUN_DEPLOYDIR}/${IMAGE_BASENAME}-${MACHINE}.lun0.ufs"
UFS_LUN1_NAME = "${UFS_LUN_DEPLOYDIR}/${IMAGE_BASENAME}-${MACHINE}.lun1.ufs"

do_image_ufs[depends] += "mtools-native:do_populate_sysroot"
do_image_ufs[depends] += "parted-native:do_populate_sysroot"
do_image_ufs[depends] += "dosfstools-native:do_populate_sysroot"

do_image_ufs[depends] += "virtual/kernel:do_deploy"
do_image_ufs[depends] += "${IMAGE_BASENAME}:do_image_ext4"
do_image_ufs[depends] += " \
	${@d.getVar('TFA_IMAGE_FILE_UFS', True) and d.getVar('TFA_IMAGE_UFS', True) + ':do_deploy' or ''} \
"

addtask image_ufs before do_image_complete after do_deploy

########################################################
# General UFS settings
########################################################


OPTIMAL_ALIGNMENT_KIB = "1024"
SECTOR_SIZE ?= "4096"
SECTOR_ALIGNMENT = "$(echo "${OPTIMAL_ALIGNMENT_KIB} * 1024 / ${SECTOR_SIZE}" | bc)"

GPT_HEADER_NUM_SECTORS = "1"
# the standard size of the partition entry array is 16K,
# regardless of the SECTOR_SIZE
PARTITION_ENTRY_ARRAY_NUM_SECTORS = "$(echo "16 * 1024 / ${SECTOR_SIZE}" | bc)"

# compute the size of a GPT Header + Partition Entry Array
GPT_SIZE_KIB = "$(echo "(${GPT_HEADER_NUM_SECTORS} + ${PARTITION_ENTRY_ARRAY_NUM_SECTORS}) * ${SECTOR_SIZE} / 1024" | bc)"

# for optimal performance, it is recommended that the starting sector 
# for a partition is a multiple of SECTOR_ALIGNMENT, such that START_SECTOR % SECTOR_ALIGNMENT == 0
LUN_FIRST_PARTITION_START_SECTOR = "${SECTOR_ALIGNMENT}"
LUN_FIRST_PARTITION_START_KIB = "$(convert_sectors_to_kib "${LUN_FIRST_PARTITION_START_SECTOR}")"

# create a partition with given name, start address,
# size and fstype
create_partition () {
	LUN_NAME="$1"
	PARTITION_NAME="$2"
	PARTITION_START_ADDRESS="$3"
	PARTITION_SIZE="$4"
	PARTITION_FSTYPE="$5"

	parted -s "${LUN_NAME}" unit KiB mkpart "${PARTITION_NAME}" "${PARTITION_FSTYPE}" "${PARTITION_START_ADDRESS}" "$(echo "${PARTITION_START_ADDRESS} + ${PARTITION_SIZE}" | bc)"

}

# write an artifact to a partition, at a given offset
write_partition () {
	INPUT_ARTIFACT="$1"
	LUN_NAME="$2"
	SIZE_KiB="$3"
	PARTITION_OFFSET="$4"
	
	dd if="${INPUT_ARTIFACT}" of="${LUN_NAME}" bs=1KiB count="${SIZE_KiB}" seek="${PARTITION_OFFSET}" conv=fsync,notrunc
}

# print LUN layout for debugging purposes
print_lun_layout () {
	lun_layout=$(parted -s "$1" unit KiB print)
	bbnote "${lun_layout}"
}

# function to determine the next GPT partition
# start sector for optimal alignment,
# with respect to SECTOR_ALIGNMENT
compute_next_aligned_gpt_partition_start_sector () {
	CURRENT_PARTITION_START_SECTOR="$1"
	CURRENT_PARTITION_SIZE_SECTORS="$2"
	
	NEXT_PARTITION_START_SECTOR="$(echo "((${CURRENT_PARTITION_START_SECTOR} + ${CURRENT_PARTITION_SIZE_SECTORS} + (${SECTOR_ALIGNMENT} - 1)) / ${SECTOR_ALIGNMENT}) * ${SECTOR_ALIGNMENT}" | bc)"

	printf "%s" "${NEXT_PARTITION_START_SECTOR}"
}

# convert KiB to number of sectors
# with respect to SECTOR_SIZE
convert_kib_to_sectors () {
	NUM_KIB="$1"

	NUM_SECTORS="$(echo "${NUM_KIB} / (${SECTOR_SIZE} / 1024)" | bc)"
	
	printf "%s" "${NUM_SECTORS}"
}

# compute a number of sectors to KiB
# with respect to SECTOR_SIZE
convert_sectors_to_kib () {
	NUM_SECTORS="$1"

	NUM_KIB="$(echo "${NUM_SECTORS} * ${SECTOR_SIZE} / 1024" | bc)"
	
	printf "%s" "${NUM_KIB}"
}

# compute the size of an artifact in KiB
compute_artifact_size_kib () {
	ARTIFACT_LOCATION="$1"

	ARTIFACT_DIMENSION_BYTES="$(stat -L -c "%s" "${ARTIFACT_LOCATION}")"
	ARTIFACT_DIMENSION_KIB="$(echo "(${ARTIFACT_DIMENSION_BYTES} + 1023) / 1024" | bc)"

	printf "%s" "${ARTIFACT_DIMENSION_KIB}"
}


########################################################
# UFS LUN0 settings
########################################################

# BL2 partition
BL2_PARTITION_NAME = "BL2"
BL2_START_ADDRESS_SECTOR = "${LUN_FIRST_PARTITION_START_SECTOR}"
BL2_START_ADDRESS_KIB = "$(convert_sectors_to_kib "${BL2_START_ADDRESS_SECTOR}")"
BL2_ARTIFACT_LOCATION = "${DEPLOY_DIR_IMAGE}/simimage/arm-trusted-firmware/bl2_w_dtb.bin"
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
KERNEL_DEVICETREE_BASENAME = "$(basename ${KERNEL_DEVICETREE})"
KERNEL_DEVICETREE_ARTIFACT = "${DEPLOY_DIR_IMAGE}/simimage/kernel/${KERNEL_DEVICETREE_BASENAME}"
KERNEL_DEVICETREE_SIZE_KIB = "$(compute_artifact_size_kib ${KERNEL_DEVICETREE_ARTIFACT})"
UBOOT_ENV_SIZE_KIB ?= "16"
FIRMWARES_PARTITION_EXTRA_SPACE_KIB ?= "256"
FIRMWARES_PARTITION_SIZE_KIB = "$(echo "${KERNEL_DEVICETREE_SIZE_KIB} + ${UBOOT_ENV_SIZE_KIB} + ${FIRMWARES_PARTITION_EXTRA_SPACE_KIB}" | bc)"
FIRMWARES_PARTITION_END_ADDRESS = "$(echo "${FIRMWARES_PARTITION_START_ADDRESS_KIB} + ${FIRMWARES_PARTITION_SIZE_KIB}" | bc)"

# when computing the total size of the LUN, we should consider that after the end of the last 
# partition, there is also the alternate GPT header + table entries
LUN0_TOTAL_SIZE = "$(echo "${FIRMWARES_PARTITION_END_ADDRESS} + ${GPT_SIZE_KIB}" | bc)"

generate_nxp_ufs_lun0 () {

	# perform sanity checks for LUN0
	if [ "${BL2_ARTIFACT_SIZE_KIB}" -gt "${BL2_PARTITION_SIZE_KIB}" ]; then
		bbfatal "BL2 artifact size is greater than the partition size! Please increase the size of the BL2 partition (BL2_PARTITION_SIZE_KIB) in your local.conf file!"
	fi

	if [ "${FIP_ARTIFACT_SIZE_KIB}" -gt "${FIP_PARTITION_SIZE_KIB}"]; then
		bbfatal "FIP artifact size is greater than the partition size! Please increase the size of the FIP partition (FIP_PARTITION_SIZE_KIB) in your local.conf file!"
	fi

	dd if=/dev/zero of="${UFS_LUN0_NAME}" bs=1K count="${LUN0_TOTAL_SIZE}"

	# Create partition table for LUN0
	export PARTED_SECTOR_SIZE=${SECTOR_SIZE}
	parted -s "${UFS_LUN0_NAME}" mklabel gpt

	create_partition ${UFS_LUN0_NAME} ${BL2_PARTITION_NAME} ${BL2_START_ADDRESS_KIB} ${BL2_PARTITION_SIZE_KIB}
	create_partition ${UFS_LUN0_NAME} ${FIP_PARTITION_NAME} ${FIP_START_ADDRESS_KIB} ${FIP_PARTITION_SIZE_KIB}
	create_partition ${UFS_LUN0_NAME} ${FIRMWARES_PARTITION_NAME} ${FIRMWARES_PARTITION_START_ADDRESS_KIB} ${FIRMWARES_PARTITION_SIZE_KIB} "fat16"

	write_partition ${BL2_ARTIFACT_LOCATION} ${UFS_LUN0_NAME} ${BL2_ARTIFACT_SIZE_KIB} ${BL2_START_ADDRESS_KIB}
	write_partition ${FIP_ARTIFACT_LOCATION} ${UFS_LUN0_NAME} ${FIP_ARTIFACT_SIZE_KIB} ${FIP_START_ADDRESS_KIB}

	# create FAT filesystem
	rm -f "${UFS_LUN_DEPLOYDIR}/${FIRMWARES_PARTITION_NAME}.part"
    mkfs.vfat -n ${FIRMWARES_PARTITION_NAME} -S ${SECTOR_SIZE} -C "${UFS_LUN_DEPLOYDIR}/${FIRMWARES_PARTITION_NAME}.part" ${FIRMWARES_PARTITION_SIZE_KIB}
	mcopy -i "${UFS_LUN_DEPLOYDIR}/${FIRMWARES_PARTITION_NAME}.part" -s "${KERNEL_DEVICETREE_ARTIFACT}" "::/${KERNEL_DEVICETREE_BASENAME}"

	write_partition "${UFS_LUN_DEPLOYDIR}/${FIRMWARES_PARTITION_NAME}.part" "${UFS_LUN0_NAME}" "${FIRMWARES_PARTITION_SIZE_KIB}" "${FIRMWARES_PARTITION_START_ADDRESS_KIB}"

	rm -f "${UFS_LUN_DEPLOYDIR}/${FIRMWARES_PARTITION_NAME}.part"

	print_lun_layout ${UFS_LUN0_NAME}
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
ROOTFS_PARTITION_END_ADDRESS = "$(echo "${ROOTFS_PARTITION_START_ADDRESS} + ${ROOTFS_PARTITION_SIZE_KIB}" | bc)"

# when computing the total size of the LUN, we should consider that after the end of the last 
# partition, there is also the alternate GPT header + partition entry array
LUN1_TOTAL_SIZE = "$(echo "${ROOTFS_PARTITION_END_ADDRESS} + ${GPT_SIZE_KIB}" | bc)"

generate_nxp_ufs_lun1 () {
	dd if=/dev/zero of="${UFS_LUN1_NAME}" bs=1K count=${LUN1_TOTAL_SIZE}

	export PARTED_SECTOR_SIZE=${SECTOR_SIZE}
	parted -s "${UFS_LUN1_NAME}" mklabel gpt

	create_partition ${UFS_LUN1_NAME} ${ESP_PARTITION_NAME} ${ESP_PARTITION_START_ADDRESS_KIB} ${ESP_PARTITION_SIZE_KIB} "fat16"
	parted -s "${UFS_LUN1_NAME}" set 1 esp on
	create_partition ${UFS_LUN1_NAME} ${ROOTFS_PARTITION_NAME} ${ROOTFS_PARTITION_START_ADDRESS} ${ROOTFS_PARTITION_SIZE_KIB} "ext4"

	# prepare the linux kernel partition
	rm -f "${UFS_LUN_DEPLOYDIR}/kernel.part"
	mkfs.vfat -n boot -S ${SECTOR_SIZE} -C "${UFS_LUN_DEPLOYDIR}/kernel.part" ${ESP_PARTITION_SIZE_KIB}
	mcopy -i "${UFS_LUN_DEPLOYDIR}/kernel.part" -s "${ESP_ARTIFACT_NAME}" "::/${KERNEL_IMAGETYPE}"

	write_partition "${UFS_LUN_DEPLOYDIR}/kernel.part" ${UFS_LUN1_NAME} ${ESP_PARTITION_SIZE_KIB} ${ESP_PARTITION_START_ADDRESS_KIB}
	write_partition ${ROOTFS_EXT4_ARTIFACT_NAME} ${UFS_LUN1_NAME} ${ROOTFS_PARTITION_SIZE_KIB} ${ROOTFS_PARTITION_START_ADDRESS}

	rm -f "${UFS_LUN_DEPLOYDIR}/kernel.part"

	print_lun_layout ${UFS_LUN1_NAME}
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
