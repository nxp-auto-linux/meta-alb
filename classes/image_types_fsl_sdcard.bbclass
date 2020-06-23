inherit image_types_fsl

IMAGE_TYPES += "sdcard"

# Boot partition volume id
BOOTDD_VOLUME_ID ?= "boot_${MACHINE}"

UBOOT_REALSUFFIX_SDCARD ?= ".${UBOOT_SUFFIX_SDCARD}"
IMAGE_BOOTLOADER ?= "${@d.getVar('PREFERRED_PROVIDER_virtual/bootloader', True) or 'u-boot'}"

UBOOT_TYPE_SDCARD ?= "sdcard"
UBOOT_BASENAME_SDCARD ?= "u-boot"
UBOOT_NAME_SDCARD ?= "${UBOOT_BASENAME_SDCARD}-${MACHINE}${UBOOT_REALSUFFIX_SDCARD}-${UBOOT_TYPE_SDCARD}"
UBOOT_KERNEL_IMAGETYPE ?= "${KERNEL_IMAGETYPE}"
UBOOT_BOOTSPACE_SEEK ?= "2"

UBOOT_ENV_SDCARD_OFFSET ?= ""
UBOOT_ENV_SDCARD ?= "u-boot-environment"
UBOOT_ENV_NAME ??= "u-boot-flashenv-sd"
UBOOT_ENV_SDCARD_FILE ?= "${@d.getVar('UBOOT_ENV_NAME', True) and (d.getVar('UBOOT_ENV_NAME', True).split()[0] + '-${MACHINE}.bin') or ''}"

# For integration of raw flash like elements we fall back to the same
# variables as for the flash class. This permits using one set of
# variables in the machine definition mostly for different types of
# booting.
FLASHIMAGE_EXTRA1_FILE ?= ""
FLASHIMAGE_EXTRA2_FILE ?= ""
FLASHIMAGE_EXTRA3_FILE ?= ""
FLASHIMAGE_EXTRA4_FILE ?= ""
FLASHIMAGE_EXTRA5_FILE ?= ""
FLASHIMAGE_EXTRA6_FILE ?= ""
FLASHIMAGE_EXTRA7_FILE ?= ""
FLASHIMAGE_EXTRA8_FILE ?= ""
FLASHIMAGE_EXTRA9_FILE ?= ""
SDCARDIMAGE_EXTRA1 ?= "${FLASHIMAGE_EXTRA1}"
SDCARDIMAGE_EXTRA1_FILE ?= "${FLASHIMAGE_EXTRA1_FILE}"
SDCARDIMAGE_EXTRA1_OFFSET ?= "${FLASHIMAGE_EXTRA1_OFFSET}"
SDCARDIMAGE_EXTRA2 ?= "${FLASHIMAGE_EXTRA2}"
SDCARDIMAGE_EXTRA2_FILE ?= "${FLASHIMAGE_EXTRA2_FILE}"
SDCARDIMAGE_EXTRA2_OFFSET ?= "${FLASHIMAGE_EXTRA2_OFFSET}"
SDCARDIMAGE_EXTRA3 ?= "${FLASHIMAGE_EXTRA3}"
SDCARDIMAGE_EXTRA3_FILE ?= "${FLASHIMAGE_EXTRA3_FILE}"
SDCARDIMAGE_EXTRA3_OFFSET ?= "${FLASHIMAGE_EXTRA3_OFFSET}"
SDCARDIMAGE_EXTRA4 ?= "${FLASHIMAGE_EXTRA4}"
SDCARDIMAGE_EXTRA4_FILE ?= "${FLASHIMAGE_EXTRA4_FILE}"
SDCARDIMAGE_EXTRA4_OFFSET ?= "${FLASHIMAGE_EXTRA4_OFFSET}"
SDCARDIMAGE_EXTRA5 ?= "${FLASHIMAGE_EXTRA5}"
SDCARDIMAGE_EXTRA5_FILE ?= "${FLASHIMAGE_EXTRA5_FILE}"
SDCARDIMAGE_EXTRA5_OFFSET ?= "${FLASHIMAGE_EXTRA5_OFFSET}"
SDCARDIMAGE_EXTRA6 ?= "${FLASHIMAGE_EXTRA6}"
SDCARDIMAGE_EXTRA6_FILE ?= "${FLASHIMAGE_EXTRA6_FILE}"
SDCARDIMAGE_EXTRA6_OFFSET ?= "${FLASHIMAGE_EXTRA6_OFFSET}"
SDCARDIMAGE_EXTRA7 ?= "${FLASHIMAGE_EXTRA7}"
SDCARDIMAGE_EXTRA7_FILE ?= "${FLASHIMAGE_EXTRA7_FILE}"
SDCARDIMAGE_EXTRA7_OFFSET ?= "${FLASHIMAGE_EXTRA7_OFFSET}"
SDCARDIMAGE_EXTRA8 ?= "${FLASHIMAGE_EXTRA8}"
SDCARDIMAGE_EXTRA8_FILE ?= "${FLASHIMAGE_EXTRA8_FILE}"
SDCARDIMAGE_EXTRA8_OFFSET ?= "${FLASHIMAGE_EXTRA8_OFFSET}"
SDCARDIMAGE_EXTRA9 ?= "${FLASHIMAGE_EXTRA9}"
SDCARDIMAGE_EXTRA9_FILE ?= "${FLASHIMAGE_EXTRA9_FILE}"
SDCARDIMAGE_EXTRA9_OFFSET ?= "${FLASHIMAGE_EXTRA9_OFFSET}"

SDCARDIMAGE_BOOT_EXTRA1 ?= ""
SDCARDIMAGE_BOOT_EXTRA1_FILE ?= ""
SDCARDIMAGE_BOOT_EXTRA2 ?= ""
SDCARDIMAGE_BOOT_EXTRA2_FILE ?= ""

SDCARD_ROOTFS_EXTRA1 ?= ""
SDCARD_ROOTFS_EXTRA1_SIZE ?= "0"
SDCARD_ROOTFS_EXTRA2 ?= ""
SDCARD_ROOTFS_EXTRA2_SIZE ?= "0"

ATF_IMAGE ?= ""
ATF_IMAGE_FILE ?= ""

SDCARD_DEPLOYDIR ?= "${IMGDEPLOYDIR}"
SDCARD = "${SDCARD_DEPLOYDIR}/${IMAGE_NAME}.rootfs.sdcard"

# Set default alignment to 4MB [in KiB]
BASE_IMAGE_ROOTFS_ALIGNMENT ?= "4096"
SDCARD_BINARY_SPACE ?= "${BASE_IMAGE_ROOTFS_ALIGNMENT}"
IMAGE_ROOTFS_ALIGNMENT = "${SDCARD_BINARY_SPACE}"

do_image_sdcard[depends] += " \
	${@d.getVar('SDCARD_RCW', True) and d.getVar('SDCARD_RCW', True) + ':do_deploy' or ''} \
	${@d.getVar('IMAGE_BOOTLOADER', True) and d.getVar('IMAGE_BOOTLOADER', True) + ':do_deploy' or ''} \
	${@d.getVar('INITRAMFS_IMAGE', True) and d.getVar('INITRAMFS_IMAGE', True) + ':do_image_complete' or ''} \
	${@d.getVar('UBOOT_ENV_SDCARD_OFFSET', True) and d.getVar('UBOOT_ENV_SDCARD', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_EXTRA1_FILE', True) and d.getVar('SDCARDIMAGE_EXTRA1', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_EXTRA2_FILE', True) and d.getVar('SDCARDIMAGE_EXTRA2', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_EXTRA3_FILE', True) and d.getVar('SDCARDIMAGE_EXTRA3', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_EXTRA4_FILE', True) and d.getVar('SDCARDIMAGE_EXTRA4', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_EXTRA5_FILE', True) and d.getVar('SDCARDIMAGE_EXTRA5', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_EXTRA6_FILE', True) and d.getVar('SDCARDIMAGE_EXTRA6', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_EXTRA7_FILE', True) and d.getVar('SDCARDIMAGE_EXTRA7', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_EXTRA8_FILE', True) and d.getVar('SDCARDIMAGE_EXTRA8', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_EXTRA9_FILE', True) and d.getVar('SDCARDIMAGE_EXTRA9', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_BOOT_EXTRA1_FILE', True) and d.getVar('SDCARDIMAGE_BOOT_EXTRA1', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_BOOT_EXTRA2_FILE', True) and d.getVar('SDCARDIMAGE_BOOT_EXTRA2', True) + ':do_deploy' or ''} \
	${@d.getVar('ATF_IMAGE_FILE', True) and d.getVar('ATF_IMAGE', True) + ':do_deploy' or ''} \
"

do_image_sdcard[depends] += "${IMAGE_BASENAME}:do_image_ext3"

SDCARD_GENERATION_COMMAND_fsl-lsch3 = "generate_fsl_lsch3_sdcard"
SDCARD_GENERATION_COMMAND_fsl-lsch2 = "generate_fsl_lsch3_sdcard"
SDCARD_GENERATION_COMMAND_s32 = "generate_imx_sdcard"

# Add extra images in the boot partition
add_extra_boot_img() {
	BOOT_IMAGE_FILE="$1"
	BOOT_IMAGE="$2"
	if [ -n "${BOOT_IMAGE_FILE}" ]; then
		mcopy -i ${BOOT_IMAGE} -s ${DEPLOY_DIR_IMAGE}/${BOOT_IMAGE_FILE} ::/${BOOT_IMAGE_FILE}
	fi
}

# Format rootfs partition
create_rootfs_partition () {
	PART_NO="$1"
	SDCARD_ROOTFS_SIZE="$2"
	SDCARD_ROOTFS_NAME="$3"
	if [ -n "${SDCARD_ROOTFS_NAME}" ]; then
		parted -s ${SDCARD} unit KiB mkpart primary $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED} \+ ${SDCARD_ROOTFS_SIZE} \* $PART_NO \+ ${BASE_IMAGE_ROOTFS_ALIGNMENT} \* $PART_NO) \
			$(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED} \+ ${SDCARD_ROOTFS_SIZE} \* $PART_NO \+ ${BASE_IMAGE_ROOTFS_ALIGNMENT} \* $PART_NO + ${SDCARD_ROOTFS_SIZE})
	fi
}

# Burn rootfs partition to .sdcard image
write_rootfs_partition () {
	PART_NO="$1"
	SDCARD_ROOTFS_SIZE="$2"
	SDCARD_ROOTFS_NAME="$3"
	if [ -n "${SDCARD_ROOTFS_NAME}" ]; then
		dd if=${SDCARD_ROOTFS_NAME} of=${SDCARD} conv=notrunc,fsync seek=1 bs=$(expr ${BOOT_SPACE_ALIGNED} \* 1024 + ${IMAGE_ROOTFS_ALIGNMENT} \* 1024 + \
			$PART_NO \* ${BASE_IMAGE_ROOTFS_ALIGNMENT} \* 1024 + $PART_NO \* ${SDCARD_ROOTFS_SIZE} \* 1024)
	fi
}

#
# Generate the boot image with the boot scripts and required Device Tree
# files
_generate_boot_image() {
	local boot_part=$1

	# Create boot partition image
	BOOT_BLOCKS=$(LC_ALL=C parted -s ${SDCARD} unit b print \
	                  | awk "/ $boot_part / { print substr(\$4, 1, length(\$4 -1)) / 1024 }")

	# mkdosfs will sometimes use FAT16 when it is not appropriate,
	# resulting in a boot failure from SYSLINUX. Use FAT32 for
	# images larger than 512MB, otherwise let mkdosfs decide.
	if [ $(expr $BOOT_BLOCKS / 1024) -gt 512 ]; then
		FATSIZE="-F 32"
	fi

	rm -f ${WORKDIR}/boot.img
	mkfs.vfat -n "${BOOTDD_VOLUME_ID}" -S 512 ${FATSIZE} -C ${WORKDIR}/boot.img $BOOT_BLOCKS

	mcopy -i ${WORKDIR}/boot.img -s ${DEPLOY_DIR_IMAGE}/${UBOOT_KERNEL_IMAGETYPE}-${MACHINE}.bin ::/${UBOOT_KERNEL_IMAGETYPE}

	# Copy boot scripts
	for item in ${BOOT_SCRIPTS}; do
		src=`echo $item | awk -F':' '{ print $1 }'`
		dst=`echo $item | awk -F':' '{ print $2 }'`

		mcopy -i ${WORKDIR}/boot.img -s ${DEPLOY_DIR_IMAGE}/$src ::/$dst
	done

	# Copy device tree file
	if test -n "${KERNEL_DEVICETREE}"; then
		kernel_bin="`readlink ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.bin`"
		for DTS_FILE in ${KERNEL_DEVICETREE}; do
			DTS_BASE_NAME=`basename ${DTS_FILE} | awk -F "." '{print $1}'`
			DTB_PATH=""
			kernel_bin_for_dtb=""
			if [ -e "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${DTS_BASE_NAME}.dtb" ]; then
				DTB_PATH="${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${DTS_BASE_NAME}.dtb"
				kernel_bin_for_dtb="`readlink $DTB_PATH | sed "s,$DTS_BASE_NAME,${MACHINE},g;s,\.dtb$,.bin,g"`"
			else if [ -e "${DEPLOY_DIR_IMAGE}/${DTS_BASE_NAME}.dtb" ]; then
					DTB_PATH="${DEPLOY_DIR_IMAGE}/${DTS_BASE_NAME}.dtb"
					kernel_bin_for_dtb="`readlink $DTB_PATH | sed "s,$DTS_BASE_NAME,${KERNEL_IMAGETYPE},g;s,\.dtb$,.bin,g"`"
				fi
			fi
			if [ -n "$DTB_PATH" ]; then
				# match dtb and kernel image by timestamp
				if [ $kernel_bin = $kernel_bin_for_dtb ]; then
					mcopy -i ${WORKDIR}/boot.img -s "$DTB_PATH" ::/${DTS_BASE_NAME}.dtb
				fi
			else
				bbfatal "${DTS_FILE} does not exist."
			fi
		done
	fi

	# Copy extlinux.conf to images that have U-Boot Extlinux support.
	if [ "${UBOOT_EXTLINUX}" = "1" ]; then
		mmd -i ${WORKDIR}/boot.img ::/extlinux
		mcopy -i ${WORKDIR}/boot.img -s ${DEPLOY_DIR_IMAGE}/extlinux.conf ::/extlinux/extlinux.conf
	fi

	# Add extra boot images in the SDCARD boot partition
	add_extra_boot_img "${SDCARDIMAGE_BOOT_EXTRA1_FILE}" "${WORKDIR}/boot.img"
	add_extra_boot_img "${SDCARDIMAGE_BOOT_EXTRA2_FILE}" "${WORKDIR}/boot.img"
}

# Create an image that can by written onto a SD card using dd for use
# with the Layerscape 2 family of devices
#
# External variables needed:
#   ${SDCARD_ROOTFS}    - the rootfs image to incorporate
#   ${IMAGE_BOOTLOADER} - bootloader to use {u-boot, barebox}
#
# The disk layout used is:
#
#    0                      -> IMAGE_ROOTFS_ALIGNMENT            - reserved to bootloader (not partitioned)
#    IMAGE_ROOTFS_ALIGNMENT -> BOOT_SPACE                     - kernel and other data
#    BOOT_SPACE             -> SDIMG_SIZE                     - rootfs
#
#                                                     Default Free space = 1.3x
#                                                     Use IMAGE_OVERHEAD_FACTOR to add more space
#                                                     <--------->
#            64MiB              16MiB         SDIMG_ROOTFS
# <-----------------------> <----------> <---------------------->
#  ------------------------ ------------ ------------------------
# | IMAGE_ROOTFS_ALIGNMENT | BOOT_SPACE | ROOTFS_SIZE            |
#  ------------------------ ------------ ------------------------
# ^                        ^            ^                        ^                               ^
# |                        |            |                        |                               |
# 0                       64MiB   64MiB + 16MiB    64MiB + 16Mib + SDIMG_ROOTFS
generate_fsl_lsch3_sdcard () {
	# Create partition table
	parted -s ${SDCARD} mklabel msdos
	parted -s ${SDCARD} unit KiB mkpart primary fat32 ${IMAGE_ROOTFS_ALIGNMENT} $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED})
	parted -s ${SDCARD} unit KiB mkpart primary $(expr  ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED}) $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED}     \+ $ROOTFS_SIZE)
	parted ${SDCARD} print

	# Fill RCW into the boot block
	if [ -n "${SDCARD_RCW_NAME}" ]; then
	        dd if=${DEPLOY_DIR_IMAGE}/${SDCARD_RCW_NAME} of=${SDCARD} conv=notrunc seek=8 bs=512
	fi

	# Burn bootloader
	case "${IMAGE_BOOTLOADER}" in
	        u-boot*)
	        if [ -n "${SPL_BINARY}" ]; then
	                dd if=${DEPLOY_DIR_IMAGE}/${SPL_BINARY} of=${SDCARD} conv=notrunc seek=$(printf "%d" ${UBOOT_BOOTSPACE_OFFSET}) bs=1
	                dd if=${DEPLOY_DIR_IMAGE}/${UBOOT_NAME_SDCARD} of=${SDCARD} conv=notrunc seek=$(expr ${UBOOT_BOOTSPACE_OFFSET} \+ 0x11000) bs=1
	        else
	                dd if=${DEPLOY_DIR_IMAGE}/${UBOOT_NAME_SDCARD} of=${SDCARD} conv=notrunc seek=$(printf "%d" ${UBOOT_BOOTSPACE_OFFSET}) bs=1
	        fi
	        if [ -n "${UBOOT_ENV_SDCARD_OFFSET}" -a -n "${UBOOT_ENV_SDCARD_FILE}" ]; then
	                dd if=${DEPLOY_DIR_IMAGE}/${UBOOT_ENV_SDCARD_FILE} of=${SDCARD} conv=notrunc seek=$(printf "%d" ${UBOOT_ENV_SDCARD_OFFSET}) bs=1
	        fi
	        ;;
	        "")
	        ;;
	        *)
	        bberror "Unknown IMAGE_BOOTLOADER value"
	        exit 1
	        ;;
	esac

	_generate_boot_image 1

	# Burn Partition
	dd if=${WORKDIR}/boot.img of=${SDCARD} conv=notrunc,fsync seek=1 bs=$(expr ${IMAGE_ROOTFS_ALIGNMENT} \* 1024)
	dd if=${SDCARD_ROOTFS} of=${SDCARD} conv=notrunc,fsync seek=1 bs=$(expr ${BOOT_SPACE_ALIGNED} \* 1024 + ${IMAGE_ROOTFS_ALIGNMENT} \* 1024)
}

#
# Create an image that can by written onto a SD card using dd for use
# with i.MX SoC family
#
# External variables needed:
#   ${SDCARD_ROOTFS}    - the rootfs image to incorporate
#   ${IMAGE_BOOTLOADER} - bootloader to use {u-boot, barebox}
#
# The disk layout used is:
#
#    0                      -> IMAGE_ROOTFS_ALIGNMENT         - reserved to bootloader (not partitioned)
#    IMAGE_ROOTFS_ALIGNMENT -> BOOT_SPACE                     - kernel and other data
#    BOOT_SPACE             -> SDIMG_SIZE                     - rootfs
#
#                                                     Default Free space = 1.3x
#                                                     Use IMAGE_OVERHEAD_FACTOR to add more space
#                                                     <--------->                                              (optional)
#            4MiB               8MiB           SDIMG_ROOTFS0                    4MiB                          SDIMG_ROOTFSn                   4MiB
# <-----------------------> <----------> <----------------------> <----------------------------->       <----------------------> <----------------------------->
#  ------------------------ ------------ ------------------------ ------------------------------- ..... ------------------------ -------------------------------
# | IMAGE_ROOTFS_ALIGNMENT | BOOT_SPACE | ROOTFS_SIZE            | BASE_IMAGE_ROOTFS_ALIGNMENT  |       | ROOTFS_SIZE           | BASE_IMAGE_ROOTFS_ALIGNMENT  |
#  ------------------------ ------------ ------------------------ ------------------------------- ..... ------------------------ -------------------------------
# ^                        ^            ^                        ^                                      ^                                                      ^
# |                        |            |                        |                                      |                                                      |
# 0                      4096     4MiB + 8MiB       4MiB + 8Mib + SDIMG_ROOTFS            12MiB + (SDIMG_ROOTFS + 4MiB) * n                 12MiB + (SDIMG_ROOTFS + 4MiB) * (n + 1)
#
#                                       |                                                       |       |                                                      |
#                                       |                                                       |       |                                                      |
#                                        <----------------------------------------------------->  .....  <---------------------------------------------------->
#                                                                   |                                                              |
#                                                                ROOTFS0                                                        ROOTFSn

generate_imx_sdcard () {
	# Create partition table
	parted -s ${SDCARD} mklabel msdos
	parted -s ${SDCARD} unit KiB mkpart primary fat32 ${IMAGE_ROOTFS_ALIGNMENT} $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED})
	create_rootfs_partition 0 ${ROOTFS_SIZE} ${SDCARD_ROOTFS}
	create_rootfs_partition 1 ${SDCARD_ROOTFS_EXTRA1_SIZE} ${SDCARD_ROOTFS_EXTRA1}
	create_rootfs_partition 2 ${SDCARD_ROOTFS_EXTRA2_SIZE} ${SDCARD_ROOTFS_EXTRA2}
	parted ${SDCARD} print

	# Burn bootloader
	case "${IMAGE_BOOTLOADER}" in
		imx-bootlets)
		bberror "The imx-bootlets is not supported for i.MX based machines"
		exit 1
		;;
		u-boot*)
		if [ -n "${SPL_BINARY}" ]; then
			dd if=${DEPLOY_DIR_IMAGE}/${SPL_BINARY} of=${SDCARD} conv=notrunc seek=$(printf "%d" ${UBOOT_BOOTSPACE_OFFSET}) bs=1
			dd if=${DEPLOY_DIR_IMAGE}/${UBOOT_NAME_SDCARD} of=${SDCARD} conv=notrunc seek=$(expr ${UBOOT_BOOTSPACE_OFFSET} \+ 0x11000) bs=1
		else
			if [ "${UBOOT_BOOTSPACE_OFFSET}" = "0" ]; then
				# write IVT
				dd if=${DEPLOY_DIR_IMAGE}/${UBOOT_NAME_SDCARD} of=${SDCARD} conv=notrunc seek=0 bs=256 count=1
				# write the rest of u-boot code
				dd if=${DEPLOY_DIR_IMAGE}/${UBOOT_NAME_SDCARD} of=${SDCARD} conv=notrunc bs=512 seek=1 skip=1
			else
				dd if=${DEPLOY_DIR_IMAGE}/${UBOOT_NAME_SDCARD} of=${SDCARD} conv=notrunc seek=$(printf "%d" ${UBOOT_BOOTSPACE_OFFSET}) bs=1
			fi
		fi
		if [ -n "${UBOOT_ENV_SDCARD_OFFSET}" -a -n "${UBOOT_ENV_SDCARD_FILE}" ]; then
			dd if=${DEPLOY_DIR_IMAGE}/${UBOOT_ENV_SDCARD_FILE} of=${SDCARD} conv=notrunc seek=$(printf "%d" ${UBOOT_ENV_SDCARD_OFFSET}) bs=1
		fi
		;;
		barebox)
		dd if=${DEPLOY_DIR_IMAGE}/barebox-${MACHINE}.bin of=${SDCARD} conv=notrunc seek=1 skip=1 bs=512
		dd if=${DEPLOY_DIR_IMAGE}/bareboxenv-${MACHINE}.bin of=${SDCARD} conv=notrunc seek=1 bs=512k
		;;
		"")
		;;
		*)
		bberror "Unknown IMAGE_BOOTLOADER value"
		exit 1
		;;
	esac

	_generate_boot_image 1

	# Burn Partitions
	dd if=${WORKDIR}/boot.img of=${SDCARD} conv=notrunc,fsync seek=1 bs=$(expr ${IMAGE_ROOTFS_ALIGNMENT} \* 1024)
	write_rootfs_partition 0 ${ROOTFS_SIZE} ${SDCARD_ROOTFS}
	write_rootfs_partition 1 ${SDCARD_ROOTFS_EXTRA1_SIZE} ${SDCARD_ROOTFS_EXTRA1}
	write_rootfs_partition 2 ${SDCARD_ROOTFS_EXTRA2_SIZE} ${SDCARD_ROOTFS_EXTRA2}
}

generate_sdcardimage_entry() {
        GSDE_IMAGE_FILE="$1"
        GSDE_IMAGE_FILE_OFFSET_NAME="$2"
        GSDE_IMAGE_FILE_OFFSET=$(printf "%d" "$3")
        GSDE_IMAGE="$4"
        if [ -n "${GSDE_IMAGE_FILE}" ]; then
                if [ -z "${GSDE_IMAGE_FILE_OFFSET}" ]; then
                        bberror "${GSDE_IMAGE_FILE_OFFSET_NAME} is undefined. To use the 'sdcard' image it needs to be defined as byte offset."
                        exit 1
                fi
                bbnote "Generating sdcard entry at ${GSDE_IMAGE_FILE_OFFSET} for ${GSDE_IMAGE_FILE}"
                dd if=${DEPLOY_DIR_IMAGE}/${GSDE_IMAGE_FILE} of=${GSDE_IMAGE} conv=notrunc,fsync bs=32K oflag=seek_bytes seek=${GSDE_IMAGE_FILE_OFFSET}
        fi
}

IMAGE_CMD_sdcard () {

	if [ -z "${SDCARD_ROOTFS}" ]; then
		bberror "SDCARD_ROOTFS is undefined. To create an SD card image with NXP's BSP, it needs to be defined."
		exit 1
	fi

	if [ -n "${UBOOT_BOOTSPACE_OFFSET}" ]; then
		UBOOT_BOOTSPACE_OFFSET=$(printf "%d" ${UBOOT_BOOTSPACE_OFFSET})
	else
		UBOOT_BOOTSPACE_OFFSET=$(expr ${UBOOT_BOOTSPACE_SEEK} \* 512)
	fi

	# Align boot partition and calculate total SD card image size
	BOOT_SPACE_ALIGNED=$(expr ${BOOT_SPACE} + ${BASE_IMAGE_ROOTFS_ALIGNMENT} - 1)
	BOOT_SPACE_ALIGNED=$(expr ${BOOT_SPACE_ALIGNED} - ${BOOT_SPACE_ALIGNED} % ${BASE_IMAGE_ROOTFS_ALIGNMENT})

	# If the size has not been preset, we default to flash image
	# sizes if available turned into [KiB] or to a hardcoded mini
	# default of 4MB.
	if [ -z "${IMAGE_ROOTFS_ALIGNMENT}" ]; then
		if [ -n "${FLASHIMAGE_SIZE}" ]; then
			IMAGE_ROOTFS_ALIGNMENT=$(expr ${FLASHIMAGE_SIZE} \* 1024)
		else
			IMAGE_ROOTFS_ALIGNMENT=${BASE_IMAGE_ROOTFS_ALIGNMENT}
		fi
	fi
	SDCARD_SIZE=$(expr ${IMAGE_ROOTFS_ALIGNMENT} + ${BOOT_SPACE_ALIGNED} + $ROOTFS_SIZE + ${BASE_IMAGE_ROOTFS_ALIGNMENT})

	# Add size of additional rootfs partitions
	if [ -n "${SDCARD_ROOTFS_EXTRA1}" ]; then
		SDCARD_SIZE=$(expr ${SDCARD_SIZE} + ${SDCARD_ROOTFS_EXTRA1_SIZE} + ${BASE_IMAGE_ROOTFS_ALIGNMENT})
	fi
	if [ -n "${SDCARD_ROOTFS_EXTRA2}" ]; then
		SDCARD_SIZE=$(expr ${SDCARD_SIZE} + ${SDCARD_ROOTFS_EXTRA2_SIZE} + ${BASE_IMAGE_ROOTFS_ALIGNMENT})
	fi

	cd ${SDCARD_DEPLOYDIR}

	# Initialize a sparse file
	dd if=/dev/zero of=${SDCARD} bs=1 count=0 seek=$(expr 1024 \* ${SDCARD_SIZE})

	# Additional elements for the raw image, copying the approach of the flashimage class
	generate_sdcardimage_entry "${SDCARDIMAGE_EXTRA1_FILE}" "SDCARDIMAGE_EXTRA1_OFFSET" "${SDCARDIMAGE_EXTRA1_OFFSET}" "${SDCARD}"
	generate_sdcardimage_entry "${SDCARDIMAGE_EXTRA2_FILE}" "SDCARDIMAGE_EXTRA2_OFFSET" "${SDCARDIMAGE_EXTRA2_OFFSET}" "${SDCARD}"
	generate_sdcardimage_entry "${SDCARDIMAGE_EXTRA3_FILE}" "SDCARDIMAGE_EXTRA3_OFFSET" "${SDCARDIMAGE_EXTRA3_OFFSET}" "${SDCARD}"
	generate_sdcardimage_entry "${SDCARDIMAGE_EXTRA4_FILE}" "SDCARDIMAGE_EXTRA4_OFFSET" "${SDCARDIMAGE_EXTRA4_OFFSET}" "${SDCARD}"
	generate_sdcardimage_entry "${SDCARDIMAGE_EXTRA5_FILE}" "SDCARDIMAGE_EXTRA5_OFFSET" "${SDCARDIMAGE_EXTRA5_OFFSET}" "${SDCARD}"
	generate_sdcardimage_entry "${SDCARDIMAGE_EXTRA6_FILE}" "SDCARDIMAGE_EXTRA6_OFFSET" "${SDCARDIMAGE_EXTRA6_OFFSET}" "${SDCARD}"
	generate_sdcardimage_entry "${SDCARDIMAGE_EXTRA7_FILE}" "SDCARDIMAGE_EXTRA7_OFFSET" "${SDCARDIMAGE_EXTRA7_OFFSET}" "${SDCARD}"
	generate_sdcardimage_entry "${SDCARDIMAGE_EXTRA8_FILE}" "SDCARDIMAGE_EXTRA8_OFFSET" "${SDCARDIMAGE_EXTRA8_OFFSET}" "${SDCARD}"
	generate_sdcardimage_entry "${SDCARDIMAGE_EXTRA9_FILE}" "SDCARDIMAGE_EXTRA9_OFFSET" "${SDCARDIMAGE_EXTRA9_OFFSET}" "${SDCARD}"

	${SDCARD_GENERATION_COMMAND}
	cd -
}

