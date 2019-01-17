#
# This class is meant to build a binary flash image for the user
# that will directly lead to a bootable system.
# Due to the need for some flexibility in naming, a custom file is used
# here because Yocto does not support overriding of classes like it does
# for recipes unfortunately.
# On integration of an SDK, the changes here should be properly merged.
#
# Heinz Wrobel <Heinz.Wrobel@nxp.com>
#
inherit image_types
IMAGE_TYPES += "flashimage"

# We assume U-Boot always has to be there, so we provide reasonable
# default values. If someone didn't want it in the image, an override
# FLASHIMAGE_UBOOT = "" would be required.
# We do the same for the rootfs. If someone wants an itb, it should
# sufficient just to override FLASHIMAGE_ROOTFS_SUFFIX
FLASHIMAGE_UBOOT_SUFFIX ?= "bin"
FLASHIMAGE_UBOOT_REALSUFFIX ?= ".${FLASHIMAGE_UBOOT_SUFFIX}"
FLASHIMAGE_UBOOT_TYPE ?= "nor"
FLASHIMAGE_UBOOT ?= "u-boot"
FLASHIMAGE_UBOOT_BASENAME ?= "u-boot"
FLASHIMAGE_UBOOT_FILE ?= '${FLASHIMAGE_UBOOT_BASENAME}-${MACHINE}${FLASHIMAGE_UBOOT_REALSUFFIX}${@oe.utils.conditional("FLASHIMAGE_UBOOT_TYPE", "", "", "-${FLASHIMAGE_UBOOT_TYPE}", d)}'
FLASHIMAGE_KERNEL ?= "virtual/kernel"
# The FLASHIMAGE_ROOTFS recipe name is special in that it needs to be
# an image recipe, not a normal package recipe.
# If the rootfs is to be created by a package recipe, then it needs
# to be added as EXTRA file rather than using the ROOTFS variable
FLASHIMAGE_ROOTFS ?= ""
FLASHIMAGE_ROOTFS_FILE ?= ""
FLASHIMAGE_ROOTFS_SUFFIX ?= ""
FLASHIMAGE ?= "${IMAGE_NAME}.flashimage"
FLASHIMAGE_DEPLOYDIR ?= "${IMGDEPLOYDIR}"

IMAGE_TYPEDEP_flashimage_append = " ${FLASHIMAGE_ROOTFS_SUFFIX}"

do_image_flashimage[depends] += " \
        ${@d.getVar('FLASHIMAGE_RESET_FILE', True) and d.getVar('FLASHIMAGE_RESET', True) + ':do_deploy' or ''} \
        ${@d.getVar('FLASHIMAGE_UBOOT_FILE', True) and d.getVar('FLASHIMAGE_UBOOT', True) + ':do_deploy' or ''} \
        ${@d.getVar('FLASHIMAGE_KERNEL_FILE', True) and d.getVar('FLASHIMAGE_KERNEL', True) + ':do_deploy' or ''} \
        ${@d.getVar('FLASHIMAGE_ROOTFS_FILE', True) and d.getVar('FLASHIMAGE_ROOTFS', True) and d.getVar('FLASHIMAGE_ROOTFS', True) + ':do_image_complete' or ''} \
        ${@d.getVar('FLASHIMAGE_EXTRA1_FILE', True) and d.getVar('FLASHIMAGE_EXTRA1', True) + ':do_deploy' or ''} \
        ${@d.getVar('FLASHIMAGE_EXTRA2_FILE', True) and d.getVar('FLASHIMAGE_EXTRA2', True) + ':do_deploy' or ''} \
        ${@d.getVar('FLASHIMAGE_EXTRA3_FILE', True) and d.getVar('FLASHIMAGE_EXTRA3', True) + ':do_deploy' or ''} \
        ${@d.getVar('FLASHIMAGE_EXTRA4_FILE', True) and d.getVar('FLASHIMAGE_EXTRA4', True) + ':do_deploy' or ''} \
        ${@d.getVar('FLASHIMAGE_EXTRA5_FILE', True) and d.getVar('FLASHIMAGE_EXTRA5', True) + ':do_deploy' or ''} \
        ${@d.getVar('FLASHIMAGE_EXTRA6_FILE', True) and d.getVar('FLASHIMAGE_EXTRA6', True) + ':do_deploy' or ''} \
        ${@d.getVar('FLASHIMAGE_EXTRA7_FILE', True) and d.getVar('FLASHIMAGE_EXTRA7', True) + ':do_deploy' or ''} \
        ${@d.getVar('FLASHIMAGE_EXTRA8_FILE', True) and d.getVar('FLASHIMAGE_EXTRA8', True) + ':do_deploy' or ''} \
        ${@d.getVar('FLASHIMAGE_EXTRA9_FILE', True) and d.getVar('FLASHIMAGE_EXTRA9', True) + ':do_deploy' or ''} \
"

python __anonymous () {
    types = d.getVar('IMAGE_FSTYPES') or ""
    if 'flashimage' in types.split():
        depends = d.getVar("DEPENDS")
        depends = "%s bc-native" % depends
        d.setVar("DEPENDS", depends)
}
#
# Create an image that can by written to flash directly
# The input files are to be found in ${DEPLOY_DIR_IMAGE}.
#
generate_flashimage_entry() {
        FLASHIMAGE_FILE="$1"
        FLASHIMAGE_FILE_OFFSET_NAME="$2"
        FLASHIMAGE_FILE_OFFSET=$(printf "%d" "$3")
        if [ -n "${FLASHIMAGE_FILE}" ]; then
                if [ -z "${FLASHIMAGE_FILE_OFFSET}" ]; then
                        bberror "${FLASHIMAGE_FILE_OFFSET_NAME} is undefined. To use the 'flashimage' image it needs to be defined as byte offset."
                        exit 1
                fi

                if [ ! -e "${FLASHIMAGE_FILE}" ]; then
                        FLASHIMAGE_FILE="${DEPLOY_DIR_IMAGE}/${FLASHIMAGE_FILE}"
                fi

                FLASHIMAGE_FILE_SIZE=`stat -L -c "%s" "${FLASHIMAGE_FILE}"`
                FLASHIMAGE_MAX=$(printf "%d + %d\n" ${FLASHIMAGE_FILE_OFFSET} ${FLASHIMAGE_FILE_SIZE} | bc)

                if [ "${FLASHIMAGE_BANK4}" = "yes" ]; then
                        if [ ${FLASHIMAGE_FILE_OFFSET} -lt ${FLASHIMAGE_BANK4_XOR} ]; then
                                if [ ${FLASHIMAGE_MAX} -gt ${FLASHIMAGE_BANK4_XOR} ]; then
                                        bberror "${FLASHIMAGE_FILE} is reaching into flash bank 4 to ${FLASHIMAGE_MAX}. Please reduce size or turn off bank 4 in the config!"
                                        exit 1
                                fi
                        fi
                fi

                bbnote "Generating flashimage entry at ${FLASHIMAGE_FILE_OFFSET} for ${FLASHIMAGE_FILE}"
                dd if=${FLASHIMAGE_FILE} of=${FLASHIMAGE} conv=notrunc,fsync bs=32K oflag=seek_bytes seek=${FLASHIMAGE_FILE_OFFSET}
                if [ "${FLASHIMAGE_BANK4}" = "yes" ]; then
                        # Really nasty hack to avoid the problem of expr return non-zero on zero results
                        # and it's inability to support any xor operation.
                        # This only works because our xor operation really is half the overall size.
                        FLASHIMAGE_TMP=$(printf "(%d + %d) %% %d\n" ${FLASHIMAGE_FILE_OFFSET} ${FLASHIMAGE_BANK4_XOR} ${FLASHIMAGE_SIZE_D} | bc)
                        bbnote "Generating flashimage entry at ${FLASHIMAGE_TMP} for ${FLASHIMAGE_FILE}"
                        dd if=${FLASHIMAGE_FILE} of=${FLASHIMAGE} conv=notrunc,fsync bs=32K oflag=seek_bytes seek=${FLASHIMAGE_TMP}
                fi
        fi
}

generate_flashimage() {
        FLASHIMAGE_SIZE_D=$(printf "%d * 1024 * 1024\n" ${FLASHIMAGE_SIZE} | bc)
        FLASHIMAGE_BANK4_XOR=$(expr ${FLASHIMAGE_SIZE_D} / 2)

        generate_flashimage_entry "${FLASHIMAGE_RESET_FILE}"  "FLASHIMAGE_RESET_OFFSET"  "${FLASHIMAGE_RESET_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_UBOOT_FILE}"  "FLASHIMAGE_UBOOT_OFFSET"  "${FLASHIMAGE_UBOOT_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_KERNEL_FILE}" "FLASHIMAGE_KERNEL_OFFSET" "${FLASHIMAGE_KERNEL_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_DTB_FILE}"    "FLASHIMAGE_DTB_OFFSET"    "${FLASHIMAGE_DTB_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_ROOTFS_FILE}" "FLASHIMAGE_ROOTFS_OFFSET" "${FLASHIMAGE_ROOTFS_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_EXTRA1_FILE}" "FLASHIMAGE_EXTRA1_OFFSET" "${FLASHIMAGE_EXTRA1_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_EXTRA2_FILE}" "FLASHIMAGE_EXTRA2_OFFSET" "${FLASHIMAGE_EXTRA2_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_EXTRA3_FILE}" "FLASHIMAGE_EXTRA3_OFFSET" "${FLASHIMAGE_EXTRA3_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_EXTRA4_FILE}" "FLASHIMAGE_EXTRA4_OFFSET" "${FLASHIMAGE_EXTRA4_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_EXTRA5_FILE}" "FLASHIMAGE_EXTRA5_OFFSET" "${FLASHIMAGE_EXTRA5_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_EXTRA6_FILE}" "FLASHIMAGE_EXTRA6_OFFSET" "${FLASHIMAGE_EXTRA6_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_EXTRA7_FILE}" "FLASHIMAGE_EXTRA7_OFFSET" "${FLASHIMAGE_EXTRA7_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_EXTRA8_FILE}" "FLASHIMAGE_EXTRA8_OFFSET" "${FLASHIMAGE_EXTRA8_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_EXTRA9_FILE}" "FLASHIMAGE_EXTRA9_OFFSET" "${FLASHIMAGE_EXTRA9_OFFSET}"
}

IMAGE_CMD_flashimage () {
        # we expect image size in Mb
        FLASH_IBS="1M"
        if [ -z "${FLASHIMAGE_SIZE}" ]; then
                if [ -n "${FLASHIMAGE_ROOTFS_FILE}" ]; then
                        FLASHIMAGE_ROOTFS_SIZE=$(stat -L -c "%s" ${FLASHIMAGE_ROOTFS_FILE})
                        FLASHIMAGE_ROOTFS_SIZE_EXTRA=$(echo "$FLASHIMAGE_ROOTFS_SIZE+(16-$FLASHIMAGE_ROOTFS_SIZE%16)"| bc)
                        FLASHIMAGE_SIZE=$(expr ${FLASHIMAGE_ROOTFS_OFFSET} + $FLASHIMAGE_ROOTFS_SIZE_EXTRA)
                        # computed size is not in Mb, so adjust the block size
                        FLASH_IBS="1"
                else
                        bberror "FLASHIMAGE_SIZE is undefined. To use the 'flashimage' image it needs to be defined in MiB units."
                        exit 1
                fi
        fi

        # Initialize the image file with all 0xff to optimize flashing
        cd ${FLASHIMAGE_DEPLOYDIR}
        dd if=/dev/zero ibs=${FLASH_IBS} count=$(printf "%d" ${FLASHIMAGE_SIZE}) | tr "\000" "\377" >${FLASHIMAGE} 
        ln -sf ${FLASHIMAGE} ${IMAGE_LINK_NAME}.flashimage

        generate_flashimage

        cd -
}
