inherit image_types_fsl

IMAGE_TYPES += "sdcard"

# Boot partition volume id
BOOTDD_VOLUME_ID ?= "boot_${MACHINE}"

UBOOT_REALSUFFIX_SDCARD ?= ".${UBOOT_SUFFIX_SDCARD}"

UBOOT_TYPE_SDCARD ?= "sdcard"
UBOOT_BASENAME_SDCARD ?= "u-boot"
UBOOT_NAME_SDCARD ?= "${UBOOT_BASENAME_SDCARD}-${UBOOT_TYPE_SDCARD}${UBOOT_REALSUFFIX_SDCARD}"
UBOOT_KERNEL_IMAGETYPE ?= "${KERNEL_IMAGETYPE}"
UBOOT_BOOTSPACE_SEEK ?= "2"

UBOOT_ENV_SDCARD_OFFSET ?= ""
UBOOT_ENV_SDCARD ?= "u-boot-environment"
UBOOT_ENV_SDCARD_FILE ?= "u-boot-flashenv-${MACHINE}.bin"

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
	${@d.getVar('SDCARD_RCW', True) and d.getVar('SDCARD_RCW', True) + ':do_deploy' or ''} \
	${@d.getVar('INITRAMFS_IMAGE', True) and d.getVar('INITRAMFS_IMAGE', True) + ':do_rootfs' or ''} \
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
"
SDCARD_GENERATION_COMMAND_fsl-lsch3 = "generate_fsl_lsch3_sdcard"

#
# Create an image that can by written onto a SD card using dd for use
# with the Layerscape 2 family of devices
#
# External variables needed:
#   ${SDCARD_ROOTFS}    - the rootfs image to incorporate
#   ${IMAGE_BOOTLOADER} - bootloader to use {u-boot, barebox}
#
# The disk layout used is:
#
#    0                      -> SDCARD_BINARY_SPACE            - reserved to bootloader (not partitioned)
#    SDCARD_BINARY_SPACE    -> BOOT_SPACE                     - kernel and other data
#    BOOT_SPACE             -> SDIMG_SIZE                     - rootfs
#
#                                                     Default Free space = 1.3x
#                                                     Use IMAGE_OVERHEAD_FACTOR to add more space
#                                                     <--------->
#            64MiB              16MiB         SDIMG_ROOTFS
# <-----------------------> <----------> <---------------------->
#  ------------------------ ------------ ------------------------
# | SDCARD_BINARY_SPACE    | BOOT_SPACE | ROOTFS_SIZE            |
#  ------------------------ ------------ ------------------------
# ^                        ^            ^                        ^                               ^
# |                        |            |                        |                               |
# 0                       64MiB   64MiB + 16MiB    64MiB + 16Mib + SDIMG_ROOTFS
generate_fsl_lsch3_sdcard () {
        # Create partition table
        parted -s ${SDCARD} mklabel msdos
        parted -s ${SDCARD} unit KiB mkpart primary fat32 ${SDCARD_BINARY_SPACE} $(expr ${SDCARD_BINARY_SPACE} \+ ${BOOT_SPACE_ALIGNED})
        parted -s ${SDCARD} unit KiB mkpart primary $(expr  ${SDCARD_BINARY_SPACE} \+ ${BOOT_SPACE_ALIGNED}) $(expr ${SDCARD_BINARY_SPACE} \+ ${BOOT_SPACE_ALIGNED}     \+ $ROOTFS_SIZE)
        parted ${SDCARD} print

        # Fill RCW into the boot block
        if [ -n "${SDCARD_RCW_NAME}" ]; then
                dd if=${DEPLOY_DIR_IMAGE}/${SDCARD_RCW_NAME} of=${SDCARD} conv=notrunc seek=8 bs=512
        fi

        # Burn bootloader
        case "${IMAGE_BOOTLOADER}" in
                u-boot)
                if [ -n "${SPL_BINARY}" ]; then
                        dd if=${DEPLOY_DIR_IMAGE}/${SPL_BINARY} of=${SDCARD} conv=notrunc seek=$(printf "%d" ${UBOOT_BOOTSPACE_OFFSET}) bs=1
                        dd if=${DEPLOY_DIR_IMAGE}/${UBOOT_NAME_SDCARD} of=${SDCARD} conv=notrunc seek=$(expr ${UBOOT_BOOTSPACE_OFFSET} \+ 0x11000) bs=1
                else
                        dd if=${DEPLOY_DIR_IMAGE}/${UBOOT_NAME_SDCARD} of=${SDCARD} conv=notrunc seek=$(printf "%d" ${UBOOT_BOOTSPACE_OFFSET}) bs=1
                fi
                if [ -n "${UBOOT_ENV_SDCARD_OFFSET}" ]; then
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

        # Create boot partition image
        BOOT_BLOCKS=$(LC_ALL=C parted -s ${SDCARD} unit b print \
                          | awk '/ 1 / { print substr($4, 1, length($4 -1)) / 1024 }')
        rm -f ${WORKDIR}/boot.img
        mkfs.vfat -n "${BOOTDD_VOLUME_ID}" -S 512 -C ${WORKDIR}/boot.img $BOOT_BLOCKS
        mcopy -i ${WORKDIR}/boot.img -s ${DEPLOY_DIR_IMAGE}/${UBOOT_KERNEL_IMAGETYPE}-${MACHINE}.bin ::/${UBOOT_KERNEL_IMAGETYPE}

        # Copy boot scripts
        for item in ${BOOT_SCRIPTS}; do
                src=`echo $item | awk -F':' '{ print $1 }'`
                dst=`echo $item | awk -F':' '{ print $2 }'`

                mcopy -i ${WORKDIR}/boot.img -s ${DEPLOY_DIR_IMAGE}/$src ::/$dst
        done

        # Copy device tree file
        if test -n "${KERNEL_DEVICETREE}"; then
                for DTS_FILE in ${KERNEL_DEVICETREE}; do
                        DTS_BASE_NAME=`basename ${DTS_FILE} | awk -F "." '{print $1}'`
                        if [ -e "${KERNEL_IMAGETYPE}-${DTS_BASE_NAME}.dtb" ]; then
                                kernel_bin="`readlink ${KERNEL_IMAGETYPE}-${MACHINE}.bin`"
                                kernel_bin_for_dtb="`readlink ${KERNEL_IMAGETYPE}-${DTS_BASE_NAME}.dtb | sed "s,$DTS_BASE_NAME,${MACHINE},g;s,\.dtb$,.bin,g"`"
                                if [ $kernel_bin = $kernel_bin_for_dtb ]; then
                                        mcopy -i ${WORKDIR}/boot.img -s ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${DTS_BASE_NAME}.dtb ::/${DTS_BASE_NAME}.dtb
                                fi
                        fi
                done
        fi

        # Burn Partition
        dd if=${WORKDIR}/boot.img of=${SDCARD} conv=notrunc,fsync seek=1 bs=$(expr ${SDCARD_BINARY_SPACE} \* 1024)
        dd if=${SDCARD_ROOTFS} of=${SDCARD} conv=notrunc,fsync seek=1 bs=$(expr ${BOOT_SPACE_ALIGNED} \* 1024 + ${SDCARD_BINARY_SPACE} \* 1024)
}

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
