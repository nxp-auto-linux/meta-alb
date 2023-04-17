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
inherit ${@bb.utils.contains('FLASHIMAGE_DYNAMIC_OFFSETS', '1', 's32cc-flash-offsets', '', d)}
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

# Backwards compatibility hack because 'UBOOT' has been partially
# renamed to FIP instead of implementing a clean selection.
# For now it is easier here to provide the fallback as needed and go
# for FIP below
FLASHIMAGE_FIP_FILE ?= "${FLASHIMAGE_UBOOT_FILE}"
FLASHIMAGE_FIP_OFFSET ?= "${FLASHIMAGE_UBOOT_OFFSET}"

IMAGE_TYPEDEP:flashimage:append = " ${FLASHIMAGE_ROOTFS_SUFFIX}"

# Dependencies are added only if the class is actively used
python __anonymous () {
    types = d.getVar('IMAGE_FSTYPES') or ''
    if 'flashimage' in types.split():
        task = 'do_image_flashimage'
        depends = d.getVar("DEPENDS") or ''
        depends = "%s bc-native" % depends
        d.setVar("DEPENDS", depends)
        bb.debug(1, "DEPENDS is '%s'" % depends)

        depvars = [
            "FLASHIMAGE_RESET",
            "FLASHIMAGE_FIP",
            "FLASHIMAGE_KERNEL",
            "FLASHIMAGE_DTB",
            "FLASHIMAGE_ROOTFS:do_image_complete",
            "FLASHIMAGE_EXTRA1",
            "FLASHIMAGE_EXTRA2",
            "FLASHIMAGE_EXTRA3",
            "FLASHIMAGE_EXTRA4",
            "FLASHIMAGE_EXTRA5",
            "FLASHIMAGE_EXTRA6",
            "FLASHIMAGE_EXTRA7",
            "FLASHIMAGE_EXTRA8",
            "FLASHIMAGE_EXTRA9"
            ]

        dl = []
        depends = d.getVarFlag('do_image_flashimage', 'depends')
        if depends is not None:
            dl.append(depends)
        for vt in depvars:
            v,t = (vt.split(':') + 2 * [None])[:2]
            if v:
                if not t:
                    t = 'do_deploy'
                if d.getVar(v + '_FILE'):
                    dep = d.getVar(v) or ''
                    if dep:
                        dl.append("%s:%s" % (dep, t))
        if dl:
            depends = " ".join(dl)
            d.setVarFlag(task, 'depends', depends)
            bb.debug(1, "%s[depends] is '%s'" % (task, depends))

        prefuncs = d.getVarFlag(task, 'prefuncs') or ''
        prefuncs = "%s %s" % (prefuncs, bb.utils.contains('FLASHIMAGE_DYNAMIC_OFFSETS', '1' , 'update_flash_offsets', '', d))
        d.setVarFlag(task, 'prefuncs', prefuncs)
        bb.debug(1, "%s[prefuncs] is '%s'" % (task, prefuncs))
}


add_flash_region () {
        # Add a new flash region for writing an image
        # Check if there is an overlap with an existing region
        start="$1"
        size="$2"
        name="$3"

        end=$(printf "%d + %d\n" ${start} ${size} | bc)

        for entry in ${flash_regions}; do
                start0=$(printf "%s" ${entry} | cut -d '-' -f1)
                end0=$(printf "%s" ${entry} | cut -d '-' -f2)
                if [ "${start}" -lt "${end0}" ] && [ "${end}" -gt "${start0}" ]; then
                        error_str=$(printf "%s (0x%x - 0x%x) overlaps with (0x%x - 0x%x)" "${name}" "${start}" "${end}" "${start0}" "${end0}")
                        bberror "Flash regions overlap: ${error_str}"
                        exit 1
                fi
        done

        # Save regions in string using format:
        #     start0-end0 start1-end1 ...
        flash_regions="${flash_regions} ${start}-${end}"
}

#
# Create an image that can by written to flash directly
# The input files are to be found in ${DEPLOY_DIR_IMAGE}.
#
generate_flashimage_entry() {
        FLASHIMAGE_FILE="$1"
        FLASHIMAGE_FILE_OFFSET_NAME="$2"
        FLASHIMAGE_FILE_OFFSET_VARIABLE="$3"
        FLASHIMAGE_FILE_OFFSET=$(printf "%d" "$3")

        if [ -n "${FLASHIMAGE_FILE}" ]; then
                if [ -z "${FLASHIMAGE_FILE_OFFSET_VARIABLE}" ]; then
                    bberror "${FLASHIMAGE_FILE} is set but offset ${FLASHIMAGE_FILE_OFFSET_NAME} for this file inside the flashimage is undefined"
                    exit 1
                fi

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
                                        error_str=$(printf "%s is reaching into flash bank 4 to 0x%x. Please reduce size or turn off bank 4 in the config!" "${FLASHIMAGE_FILE}" ${FLASHIMAGE_MAX})
                                        bberror "${error_str}"
                                        exit 1
                                fi
                        fi
                fi

                # add region for checking overlap with existing ones
                add_flash_region "${FLASHIMAGE_FILE_OFFSET}" "${FLASHIMAGE_FILE_SIZE}" "${FLASHIMAGE_FILE_OFFSET_NAME}"

                bbnote "Generating flashimage entry at ${FLASHIMAGE_FILE_OFFSET} for ${FLASHIMAGE_FILE}"
                dd if=${FLASHIMAGE_FILE} of=${FLASHIMAGE} conv=notrunc,fsync bs=32K oflag=seek_bytes seek=${FLASHIMAGE_FILE_OFFSET}
                if [ "${FLASHIMAGE_BANK4}" = "yes" ]; then
                        # Really nasty hack to avoid the problem of expr return non-zero on zero results
                        # and it's inability to support any xor operation.
                        # This only works because our xor operation really is half the overall size.
                        FLASHIMAGE_TMP=$(printf "(%d + %d) %% %d\n" ${FLASHIMAGE_FILE_OFFSET} ${FLASHIMAGE_BANK4_XOR} ${FLASHIMAGE_SIZE_D} | bc)

                        add_flash_region "${FLASHIMAGE_TMP}" "${FLASHIMAGE_FILE_SIZE}" "${FLASHIMAGE_FILE_OFFSET_NAME}"

                        bbnote "Generating flashimage entry at ${FLASHIMAGE_TMP} for ${FLASHIMAGE_FILE}"
                        dd if=${FLASHIMAGE_FILE} of=${FLASHIMAGE} conv=notrunc,fsync bs=32K oflag=seek_bytes seek=${FLASHIMAGE_TMP}
                fi
        fi
}

generate_flashimage() {
        flash_regions=""

        FLASHIMAGE_SIZE_D=$(printf "%d * 1024 * 1024\n" ${FLASHIMAGE_SIZE} | bc)
        FLASHIMAGE_BANK4_XOR=$(expr ${FLASHIMAGE_SIZE_D} / 2)

        generate_flashimage_entry "${FLASHIMAGE_RESET_FILE}"  "FLASHIMAGE_RESET_OFFSET"  "${FLASHIMAGE_RESET_OFFSET}"
        generate_flashimage_entry "${FLASHIMAGE_FIP_FILE}"    "FLASHIMAGE_FIP_OFFSET"  "${FLASHIMAGE_FIP_OFFSET}"
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

IMAGE_CMD:flashimage () {
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
