inherit gpt_image_utils

IMAGE_TYPES += "sdcard_s32n"

DEPENDS += "bc-native"

TFA_IMAGE_EMMC ?= "arm-trusted-firmware"
TFA_IMAGE_FILE_EMMC ?= "fip.bin-sdcard"

UBOOT_ENV_EMMC ?= "u-boot-environment"
UBOOT_ENV_NAME_EMMC ?= "u-boot-flashenv-sd"

EMMC_DEPLOYDIR = "${IMGDEPLOYDIR}/simimage"
EMMC_NAME = "${EMMC_DEPLOYDIR}/${IMAGE_BASENAME}-${MACHINE}.sdcard"

do_image_sdcard_s32n[depends] += "mtools-native:do_populate_sysroot"
do_image_sdcard_s32n[depends] += "parted-native:do_populate_sysroot"
do_image_sdcard_s32n[depends] += "dosfstools-native:do_populate_sysroot"

do_image_sdcard_s32n[depends] += "virtual/kernel:do_deploy"
do_image_sdcard_s32n[depends] += "${IMAGE_BASENAME}:do_deploy"
do_image_sdcard_s32n[depends] += " \
        ${@d.getVar('UBOOT_ENV_NAME_EMMC', True) and d.getVar('UBOOT_ENV_EMMC', True) + ':do_deploy' or ''} \
		${@d.getVar('TFA_IMAGE_FILE_EMMC', True) and d.getVar('TFA_IMAGE_EMMC', True) + ':do_deploy' or ''} \
"

EMMC_SECTOR_SIZE = "512"

# BL2 partition
EMMC_BL2_PARTITION_NAME = "BL2"
EMMC_BL2_START_ADDRESS_SECTOR = "$(get_first_partition_start_sector "${EMMC_SECTOR_SIZE}")"
EMMC_BL2_START_ADDRESS_KIB = "$(convert_sectors_to_kib "${EMMC_BL2_START_ADDRESS_SECTOR}" "${EMMC_SECTOR_SIZE}")"
EMMC_BL2_ARTIFACT_LOCATION = "${DEPLOY_DIR_IMAGE}/simimage/arm-trusted-firmware/bl2_w_dtb.bin-sdcard"
EMMC_BL2_ARTIFACT_SIZE_KIB = "$(compute_artifact_size_kib ${EMMC_BL2_ARTIFACT_LOCATION})"
# we want the BL2 partition to be of fixed size (512K)
EMMC_BL2_PARTITION_SIZE_KIB ?= "512"
EMMC_BL2_PARTITION_SIZE_SECTORS = "$(convert_kib_to_sectors "${EMMC_BL2_PARTITION_SIZE_KIB}" "${EMMC_SECTOR_SIZE}")"

# FIP partition
EMMC_FIP_PARTITION_NAME = "FIP"
EMMC_FIP_START_ADDRESS_SECTOR = "$(compute_next_aligned_gpt_partition_start_sector ${EMMC_BL2_START_ADDRESS_SECTOR} ${EMMC_BL2_PARTITION_SIZE_SECTORS} ${EMMC_SECTOR_SIZE})"
EMMC_FIP_START_ADDRESS_KIB = "$(convert_sectors_to_kib ${EMMC_FIP_START_ADDRESS_SECTOR} ${EMMC_SECTOR_SIZE})"
EMMC_FIP_ARTIFACT_LOCATION = "${DEPLOY_DIR_IMAGE}/simimage/arm-trusted-firmware/${TFA_IMAGE_FILE_EMMC}"
EMMC_FIP_ARTIFACT_SIZE_KIB = "$(compute_artifact_size_kib ${EMMC_FIP_ARTIFACT_LOCATION})"
# we want the FIP partition to be of fixed size (1M)
# to accommodate a large BL33 image such as the Linux Kernel, the FIP partition must be increased
EMMC_FIP_PARTITION_SIZE_KIB = "${@bb.utils.contains('DISTRO_FEATURES', 'linux-kernel-as-bl33', '15360', '1024', d)}"

EMMC_FIP_PARTITION_SIZE_SECTORS = "$(convert_kib_to_sectors "${EMMC_FIP_PARTITION_SIZE_KIB}" "${EMMC_SECTOR_SIZE}")"

# FAT partition
EMMC_FAT_PARTITION_NAME = "FAT"
EMMC_FAT_PARTITION_START_ADDRESS_SECTOR = "$(compute_next_aligned_gpt_partition_start_sector ${EMMC_FIP_START_ADDRESS_SECTOR} ${EMMC_FIP_PARTITION_SIZE_SECTORS} ${EMMC_SECTOR_SIZE})"
EMMC_FAT_PARTITION_START_ADDRESS_KIB = "$(convert_sectors_to_kib ${EMMC_FAT_PARTITION_START_ADDRESS_SECTOR} ${EMMC_SECTOR_SIZE})"
FIRMWARES_FOLDER ?= "/FIRMWARE"
FIRMWARES_FOLDER_VENDOR ?= "${FIRMWARES_FOLDER}/NXP"
EMMC_KERNEL_DEVICETREE_BASENAME = "$(basename ${KERNEL_DEVICETREE})"
EMMC_KERNEL_DEVICETREE_ARTIFACT = "${DEPLOY_DIR_IMAGE}/simimage/kernel/${KERNEL_DEVICETREE_BASENAME}"
EMMC_KERNEL_DEVICETREE_SIZE_KIB = "$(compute_artifact_size_kib ${KERNEL_DEVICETREE_ARTIFACT})"
EMMC_KERNEL_ARTIFACT_NAME = "${DEPLOY_DIR_IMAGE}/simimage/kernel/${KERNEL_IMAGETYPE}"
EMMC_KERNEL_ARTIFACT_SIZE_KIB = "$(compute_artifact_size_kib "${EMMC_KERNEL_ARTIFACT_NAME}")"
EMMC_UBOOT_ENV_SIZE_KIB ?= "16"
UBOOT_ENV_EMMC_FILE ?= "${DEPLOY_DIR_IMAGE}/${@d.getVar('UBOOT_ENV_NAME_EMMC', True) and (d.getVar('UBOOT_ENV_NAME_EMMC', True) + '-${MACHINE}.bin') or ''}"
EMMC_FAT_PARTITION_EXTRA_SPACE_KIB ?= "256"
EMMC_FAT_PARTITION_SIZE_KIB = "$(echo "${EMMC_KERNEL_DEVICETREE_SIZE_KIB} + ${EMMC_KERNEL_ARTIFACT_SIZE_KIB} + ${EMMC_UBOOT_ENV_SIZE_KIB} + ${EMMC_FAT_PARTITION_EXTRA_SPACE_KIB}" | bc)"
EMMC_FAT_PARTITION_SIZE_SECTORS = "$(convert_kib_to_sectors "${EMMC_FAT_PARTITION_SIZE_KIB}" "${EMMC_SECTOR_SIZE}")"

# ROOTFS partition
EMMC_ROOTFS_PARTITION_NAME = "linux-rootfs"
EMMC_ROOTFS_PARTITION_START_ADDRESS_SECTOR = "$(compute_next_aligned_gpt_partition_start_sector ${EMMC_FAT_PARTITION_START_ADDRESS_SECTOR} ${EMMC_FAT_PARTITION_SIZE_SECTORS} ${EMMC_SECTOR_SIZE})"
EMMC_ROOTFS_PARTITION_START_ADDRESS_KIB = "$(convert_sectors_to_kib ${EMMC_ROOTFS_PARTITION_START_ADDRESS_SECTOR} ${EMMC_SECTOR_SIZE})"
EMMC_ROOTFS_EXT4_ARTIFACT_NAME = "${DEPLOY_DIR_IMAGE}/ufsimage/${IMAGE_BASENAME}-${MACHINE}.rootfs.ext4"
EMMC_ROOTFS_PARTITION_SIZE_KIB = "$(compute_artifact_size_kib "${EMMC_ROOTFS_EXT4_ARTIFACT_NAME}")"
EMMC_ROOTFS_PARTITION_SIZE_SECTORS = "$(convert_kib_to_sectors "${EMMC_ROOTFS_PARTITION_SIZE_KIB}" "${EMMC_SECTOR_SIZE}")"

generate_nxp_sdcard_s32n () {

	# perform sanity checks
	if [ "${EMMC_BL2_ARTIFACT_SIZE_KIB}" -gt "${EMMC_BL2_PARTITION_SIZE_KIB}" ]; then
		bbfatal "BL2 artifact size is greater than the partition size! Please increase the size of the BL2 partition (EMMC_BL2_PARTITION_SIZE_KIB) in your local.conf file!"
	fi

	if [ "${EMMC_FIP_ARTIFACT_SIZE_KIB}" -gt "${EMMC_FIP_PARTITION_SIZE_KIB}" ]; then
		bbfatal "FIP artifact size is greater than the partition size! Please increase the size of the FIP partition (EMMC_FIP_PARTITION_SIZE_KIB) in your local.conf file!"
	fi

	BL2_ROUND_SIZE_KIB=$(roundup_size_kib ${EMMC_BL2_PARTITION_SIZE_KIB} ${OPTIMAL_ALIGNMENT_KIB})
	FIP_ROUND_SIZE_KIB=$(roundup_size_kib ${EMMC_FIP_PARTITION_SIZE_KIB} ${OPTIMAL_ALIGNMENT_KIB})
	FAT_ROUND_SIZE_KIB=$(roundup_size_kib ${EMMC_FAT_PARTITION_SIZE_KIB} ${OPTIMAL_ALIGNMENT_KIB})
	ROOTFS_ROUND_SIZE_KIB=$(roundup_size_kib ${EMMC_ROOTFS_PARTITION_SIZE_KIB} ${OPTIMAL_ALIGNMENT_KIB})

	# when computing the total size of the GPT partition, we should consider that after the end of the last 
	# partition, there is also the alternate GPT header + table entries
	gpt_size_kib=$(get_gpt_size_kib ${EMMC_SECTOR_SIZE})
	gpt_round_size_kib=$(roundup_size_kib ${gpt_size_kib} ${OPTIMAL_ALIGNMENT_KIB})
	FIRST_PARTITION_START_KIB=$(get_first_partition_start_kib ${EMMC_SECTOR_SIZE})

	EMMC_TOTAL_SIZE=$(echo "${FIRST_PARTITION_START_KIB} + ${BL2_ROUND_SIZE_KIB} + ${FIP_ROUND_SIZE_KIB} + ${FAT_ROUND_SIZE_KIB} + ${ROOTFS_ROUND_SIZE_KIB} + ${gpt_round_size_kib}" | bc)

	dd if=/dev/zero of="${EMMC_NAME}" bs=1K count="${EMMC_TOTAL_SIZE}"

	# Create partition table for EMMC
	export PARTED_SECTOR_SIZE=${EMMC_SECTOR_SIZE}
	parted -s "${EMMC_NAME}" mklabel gpt

	create_partition ${EMMC_NAME} ${EMMC_SECTOR_SIZE} ${EMMC_BL2_PARTITION_NAME} ${EMMC_BL2_PARTITION_SIZE_SECTORS}
	create_partition ${EMMC_NAME} ${EMMC_SECTOR_SIZE} ${EMMC_FIP_PARTITION_NAME} ${EMMC_FIP_PARTITION_SIZE_SECTORS}
	create_partition ${EMMC_NAME} ${EMMC_SECTOR_SIZE} ${EMMC_FAT_PARTITION_NAME} ${EMMC_FAT_PARTITION_SIZE_SECTORS}
	create_partition ${EMMC_NAME} ${EMMC_SECTOR_SIZE} ${EMMC_ROOTFS_PARTITION_NAME} ${EMMC_ROOTFS_PARTITION_SIZE_SECTORS}

	write_partition ${EMMC_BL2_ARTIFACT_LOCATION} ${EMMC_NAME} ${EMMC_BL2_ARTIFACT_SIZE_KIB} ${EMMC_BL2_START_ADDRESS_KIB}
	write_partition ${EMMC_FIP_ARTIFACT_LOCATION} ${EMMC_NAME} ${EMMC_FIP_ARTIFACT_SIZE_KIB} ${EMMC_FIP_START_ADDRESS_KIB}

	# create FAT filesystem
	rm -f "${EMMC_DEPLOYDIR}/${EMMC_FAT_PARTITION_NAME}.part-mmc"
    mkfs.vfat -n ${EMMC_FAT_PARTITION_NAME} -S ${EMMC_SECTOR_SIZE} -C "${EMMC_DEPLOYDIR}/${EMMC_FAT_PARTITION_NAME}.part-mmc" ${EMMC_FAT_PARTITION_SIZE_KIB}
	mmd -i "${UFS_LUN_DEPLOYDIR}/${EMMC_FAT_PARTITION_NAME}.part-mmc" "::${FIRMWARES_FOLDER}" "::${FIRMWARES_FOLDER_VENDOR}" "::${FIRMWARES_FOLDER_VENDOR}/${MACHINE}" "::${FIRMWARES_FOLDER_VENDOR}/${MACHINE}/linux" "::${FIRMWARES_FOLDER_VENDOR}/${MACHINE}/u-boot" "::/BOOT" "::/BOOT/linux"
	mcopy -i "${EMMC_DEPLOYDIR}/${EMMC_FAT_PARTITION_NAME}.part-mmc" -s "${EMMC_KERNEL_DEVICETREE_ARTIFACT}" "::${FIRMWARES_FOLDER_VENDOR}/${MACHINE}/linux/${EMMC_KERNEL_DEVICETREE_BASENAME}"
	mcopy -i "${EMMC_DEPLOYDIR}/${EMMC_FAT_PARTITION_NAME}.part-mmc" -s "${UBOOT_ENV_EMMC_FILE}" "::${FIRMWARES_FOLDER_VENDOR}/${MACHINE}/u-boot/env.txt"
	mcopy -i "${EMMC_DEPLOYDIR}/${EMMC_FAT_PARTITION_NAME}.part-mmc" -s "${EMMC_KERNEL_ARTIFACT_NAME}" "::/BOOT/linux/${KERNEL_IMAGETYPE}"
	write_partition "${EMMC_DEPLOYDIR}/${EMMC_FAT_PARTITION_NAME}.part-mmc" "${EMMC_NAME}" "${EMMC_FAT_PARTITION_SIZE_KIB}" "${EMMC_FAT_PARTITION_START_ADDRESS_KIB}"
	rm -f "${EMMC_DEPLOYDIR}/${EMMC_FAT_PARTITION_NAME}.part-mmc"

	write_partition ${EMMC_ROOTFS_EXT4_ARTIFACT_NAME} ${EMMC_NAME} ${EMMC_ROOTFS_PARTITION_SIZE_KIB} ${EMMC_ROOTFS_PARTITION_START_ADDRESS_KIB}
}

IMAGE_CMD:sdcard_s32n () {
	mkdir -p "${EMMC_DEPLOYDIR}"
	generate_nxp_sdcard_s32n
}
