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
inherit image_types_fsl
IMAGE_TYPES += "flashimage"

# We assume U-Boot always has to be there, so we provide reasonable
# default values. If someone didn't want it in the image, an override
# FLASHIMAGE_UBOOT = "" would be required.
# We do the same for the rootfs. If someone wants an itb, it should
# sufficient just to override FLASHIMAGE_ROOTFS_SUFFIX
FLASHIMAGE_UBOOT_SUFFIX ?= "bin"
FLASHIMAGE_UBOOT ?= "${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}.${FLASHIMAGE_UBOOT_SUFFIX}"
FLASHIMAGE_KERNEL ?= "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}"
FLASHIMAGE_DTB_SUFFIX ?= "dtb"
FLASHIMAGE_DTB ?= "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.${FLASHIMAGE_DTB_SUFFIX}"
FLASHIMAGE_ROOTFS_SUFFIX ?= "ext3.gz.u-boot"
FLASHIMAGE_ROOTFS ?= "${IMGDEPLOYDIR}/${IMAGE_NAME}.rootfs.${FLASHIMAGE_ROOTFS_SUFFIX}"
FLASHIMAGE = "${IMGDEPLOYDIR}/${IMAGE_NAME}.rootfs.flashimage"

IMAGE_BOOTLOADER ?= "u-boot"

do_image_flashimage[depends] ?= "virtual/kernel:do_deploy \
                        ${@d.getVar('IMAGE_BOOTLOADER', True) and d.getVar('IMAGE_BOOTLOADER', True) + ':do_deploy' or ''} \
                        bc-native:do_populate_sysroot"

# The flashimage requires the rootfs filesystem to be built before using
# it so we must make this dependency explicit.
# Due to the original line not being capable of dealing with complex
# extensions and multiple dots properly, we use a python function for
# properly setting up the image dependency.
IMAGE_TYPEDEP_flashimage = "${FLASHIMAGE_ROOTFS_SUFFIX}"
#
# Create an image that can by written to flash directly
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
                bbnote "Generating flashimage entry at ${FLASHIMAGE_FILE_OFFSET} for ${FLASHIMAGE_FILE}"
                dd if=${FLASHIMAGE_FILE} of=${FLASHIMAGE} conv=notrunc,fsync bs=1 seek=${FLASHIMAGE_FILE_OFFSET}
                if [ "${FLASHIMAGE_BANK4}" = "yes" ]; then
                        # Really nasty hack to avoid the problem of expr return non-zero on zero results
                        # and it's inability to support any xor operation.
                        # This only works because our xor operation really is half the overall size.
                        FLASHIMAGE_TMP=$(printf "(%d + %d) %% %d\n" ${FLASHIMAGE_FILE_OFFSET} ${FLASHIMAGE_BANK4_XOR} ${FLASHIMAGE_SIZE_D} | bc)
                        bbnote "Generating flashimage entry at ${FLASHIMAGE_TMP} for ${FLASHIMAGE_FILE}"
                        dd if=${FLASHIMAGE_FILE} of=${FLASHIMAGE} conv=notrunc,fsync bs=1 seek=${FLASHIMAGE_TMP}
                fi
        fi
}

generate_flashimage() {
        FLASHIMAGE_SIZE_D=$(printf "%d * 1024 * 1024\n" ${FLASHIMAGE_SIZE} | bc)
        FLASHIMAGE_BANK4_XOR=$(expr ${FLASHIMAGE_SIZE_D} / 2)

        generate_flashimage_entry "${FLASHIMAGE_RESET}"  "FLASHIMAGE_RESET_OFFSET"  "${FLASHIMAGE_RESET_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_UBOOT}"  "FLASHIMAGE_UBOOT_OFFSET"  "${FLASHIMAGE_UBOOT_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_KERNEL}" "FLASHIMAGE_KERNEL_OFFSET" "${FLASHIMAGE_KERNEL_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_DTB}"    "FLASHIMAGE_DTB_OFFSET"    "${FLASHIMAGE_DTB_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_EXTRA1}" "FLASHIMAGE_EXTRA1_OFFSET" "${FLASHIMAGE_EXTRA1_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_EXTRA2}" "FLASHIMAGE_EXTRA2_OFFSET" "${FLASHIMAGE_EXTRA2_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_EXTRA3}" "FLASHIMAGE_EXTRA3_OFFSET" "${FLASHIMAGE_EXTRA3_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_EXTRA4}" "FLASHIMAGE_EXTRA4_OFFSET" "${FLASHIMAGE_EXTRA4_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_EXTRA5}" "FLASHIMAGE_EXTRA5_OFFSET" "${FLASHIMAGE_EXTRA5_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_EXTRA6}" "FLASHIMAGE_EXTRA6_OFFSET" "${FLASHIMAGE_EXTRA6_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_EXTRA7}" "FLASHIMAGE_EXTRA7_OFFSET" "${FLASHIMAGE_EXTRA7_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_EXTRA8}" "FLASHIMAGE_EXTRA8_OFFSET" "${FLASHIMAGE_EXTRA8_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_EXTRA9}" "FLASHIMAGE_EXTRA9_OFFSET" "${FLASHIMAGE_EXTRA9_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_ROOTFS}" "FLASHIMAGE_ROOTFS_OFFSET" "${FLASHIMAGE_ROOTFS_OFFSET}"


}

IMAGE_CMD_flashimage () {
        FLASHIMAGE_ROOTFS_SIZE=$(stat -c%s ${FLASHIMAGE_ROOTFS})
        FLASHIMAGE_ROOTFS_SIZE_EXTRA=$(echo "$FLASHIMAGE_ROOTFS_SIZE+(16-$FLASHIMAGE_ROOTFS_SIZE%16)"| bc)
        FLASHIMAGE_SIZE=$(expr ${FLASHIMAGE_ROOTFS_OFFSET} + $FLASHIMAGE_ROOTFS_SIZE_EXTRA)
        # Initialize a sparse file
        dd if=/dev/zero ibs=1 count=$(printf "%d" ${FLASHIMAGE_SIZE}) | tr "\000" "\377" >${FLASHIMAGE}
        generate_flashimage
}
