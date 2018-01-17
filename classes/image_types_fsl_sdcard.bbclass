inherit image_types_fsl

IMAGE_TYPES += "sdcard"

# Boot partition volume id
BOOTDD_VOLUME_ID ?= "boot_${MACHINE}"

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

do_image_sdcard[depends] += " \
	${@d.getVar('SDCARDIMAGE_EXTRA1_FILE', True) and d.getVar('SDCARDIMAGE_EXTRA1', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_EXTRA2_FILE', True) and d.getVar('SDCARDIMAGE_EXTRA2', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_EXTRA3_FILE', True) and d.getVar('SDCARDIMAGE_EXTRA3', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_EXTRA4_FILE', True) and d.getVar('SDCARDIMAGE_EXTRA4', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_EXTRA5_FILE', True) and d.getVar('SDCARDIMAGE_EXTRA5', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_EXTRA6_FILE', True) and d.getVar('SDCARDIMAGE_EXTRA6', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_EXTRA7_FILE', True) and d.getVar('SDCARDIMAGE_EXTRA7', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_EXTRA8_FILE', True) and d.getVar('SDCARDIMAGE_EXTRA8', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_EXTRA9_FILE', True) and d.getVar('SDCARDIMAGE_EXTRA9', True) + ':do_deploy' or ''} \
"

generate_sdcardimage_entry() {
        FLASHIMAGE_FILE="$1"
        FLASHIMAGE_FILE_OFFSET_NAME="$2"
        FLASHIMAGE_FILE_OFFSET=$(printf "%d" "$3")
        FLASHIMAGE="$4"
        if [ -n "${FLASHIMAGE_FILE}" ]; then
                if [ -z "${FLASHIMAGE_FILE_OFFSET}" ]; then
                        bberror "${FLASHIMAGE_FILE_OFFSET_NAME} is undefined. To use the 'sdcard' image it needs to be defined as byte offset."
                        exit 1
                fi
                bbnote "Generating sdcard entry at ${FLASHIMAGE_FILE_OFFSET} for ${FLASHIMAGE_FILE}"
                dd if=${FLASHIMAGE_FILE} of=${FLASHIMAGE} conv=notrunc,fsync bs=1 seek=${FLASHIMAGE_FILE_OFFSET}
        fi
}

IMAGE_CMD_sdcard () {
	bbwarn "The '${MACHINE}' is using the i.MX 'sdcard' image format which is deprecated. This image type is going to be removed in next release so please convert this machine to use the wic tool"

	if [ -z "${SDCARD_ROOTFS}" ]; then
		bberror "SDCARD_ROOTFS is undefined. To use sdcard image from Freescale's BSP it needs to be defined."
		exit 1
	fi

	# Align boot partition and calculate total SD card image size
	BOOT_SPACE_ALIGNED=$(expr ${BOOT_SPACE} + ${IMAGE_ROOTFS_ALIGNMENT} - 1)
	BOOT_SPACE_ALIGNED=$(expr ${BOOT_SPACE_ALIGNED} - ${BOOT_SPACE_ALIGNED} % ${IMAGE_ROOTFS_ALIGNMENT})
	SDCARD_SIZE=$(expr ${IMAGE_ROOTFS_ALIGNMENT} + ${BOOT_SPACE_ALIGNED} + $ROOTFS_SIZE + ${IMAGE_ROOTFS_ALIGNMENT})

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
}
