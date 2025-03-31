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
inherit rawimageutils
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


#
# Create an image that can by written to flash directly
# The input files are to be found in ${DEPLOY_DIR_IMAGE}.
#
generate_flashimage_entry() {
        file="$1"
        if [ -n "${file}" ]; then
                file="${DEPLOY_DIR_IMAGE}/${file}"
                rawimage_generate_entry "${file}" "${FLASHIMAGE}" "$2" "$3"
        fi
}

generate_flashimage() {
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

trim_flashimage_bootloader() {
	flashimage="$1"
	start_offset=$(printf "%d" "$2")

	if [ -z "${flashimage}" ]; then
		bberror "${flashimage} is undefined"
		exit 1
	fi

	if [ -z "${start_offset}" -o "${start_offset}" -eq 0 ]; then
		bberror "${start_offset} is unset or it starts from 0x0"
		exit 1
	fi

	end_offset=$(printf "%d" "${FLASHIMAGE_END_OFFSET}")
	if [ -z "${end_offset}" -o "${end_offset}" -lt ${start_offset} ]; then
		bberror "${end_offset} is unset or it's less than the start_offset: ${start_offset}"
		exit 1
	fi

	trimmed_size=$(expr "${end_offset}" - "${start_offset}")
	cp "${flashimage}" "${tmp}" || (bberror "Failed to create temporary copy of flashimage" && exit 1)
	dd if="${tmp}" of="${flashimage}" bs=1 skip="${start_offset}" count="${trimmed_size}"
	rm -f "${tmp}" || exit 1
}

IMAGE_CMD:flashimage () {
        # we expect image size in MiB
        FLASH_IBS="1048576"
        if [ -z "${FLASHIMAGE_SIZE}" ]; then
                if [ -n "${FLASHIMAGE_ROOTFS_FILE}" ]; then
                        FLASHIMAGE_ROOTFS_SIZE=$(stat -L -c "%s" "${FLASHIMAGE_ROOTFS_FILE}")
                        FLASHIMAGE_ROOTFS_SIZE_EXTRA=$(echo "$FLASHIMAGE_ROOTFS_SIZE+(16-$FLASHIMAGE_ROOTFS_SIZE%16)"| bc)
                        FLASHIMAGE_SIZE=$(expr ${FLASHIMAGE_ROOTFS_OFFSET} + $FLASHIMAGE_ROOTFS_SIZE_EXTRA)
                        # computed size is not in MiB, so adjust the block size
                        FLASH_IBS="1"
                else
                        bberror "FLASHIMAGE_SIZE is undefined. To use the flash image it needs to be defined in decimal MiB units."
                        exit 1
                fi
        fi

        FLASHIMAGE_SIZE_D=$(printf "%s * %s\n" ${FLASHIMAGE_SIZE} ${FLASH_IBS} | bc)
        rawimage_initutils "flash image" "${FLASHIMAGE_BANK4}" ${FLASHIMAGE_SIZE_D};

        # Initialize the image file with all 0xff to optimize flashing
        cd ${FLASHIMAGE_DEPLOYDIR}
        dd if=/dev/zero ibs=$(printf "%d" ${FLASHIMAGE_SIZE_D}) count=1 | tr "\000" "\377" > "${FLASHIMAGE}"
        ln -sf "${FLASHIMAGE}" "${IMAGE_LINK_NAME}.flashimage"

        generate_flashimage

        if [ "${FLASHIMAGE_TRIM_BOOTLOADER}" = "1" ]; then
            trim_flashimage_bootloader "${FLASHIMAGE}" "${FLASHIMAGE_FIP_OFFSET}"
        fi

        cd -
}
