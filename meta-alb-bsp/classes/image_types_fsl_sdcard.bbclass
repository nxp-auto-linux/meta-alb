inherit image_types_fsl
inherit rawimageutils

IMAGE_TYPES += "sdcard"

SDCARD_SECTOR_SIZE ??= "512"

# The variables below have the corresponding values assigned in conf/machine/include .inc files
# Path to boot files (e.g. Image) for the FAT partition
SD_FAT_BOOT_DIR ??= ""

# Path to device tree blob for the FAT partition
SD_FAT_DTB_DIR ??= ""

# Decide whether to include the BL2 partition or not (0 or 1)
SD_INCLUDE_BL2_PART ??= "0"

# Index of partition where FIP image will be written (1 or 2)
SD_FIP_BURN_PART_INDEX ??= "0"

# Size of the FIP partition on the SDCard (in KB)
SD_FW_SIZE_ALIGNED ??= "0"

# Path to the FIP artifact location
SD_FIP_PATH ??= ""

BL2_NAME_SDCARD ??= ""

# Path to the BL2 artifact location
SD_BL2_PATH ??= ""

SDCARD_PARTITIONING_SCHEME ??= ""

SDCARD_BL2_PARTITION_NAME ??= ""
SDCARD_FIP_PARTITION_NAME ??= ""
SDCARD_FAT_PARTITION_NAME ??= ""
SDCARD_ROOTFS_PARTITION_NAME ??= ""

DEPENDS += "bc-native"
DEPENDS:append = "${@' gptfdisk-native' if d.getVar('SDCARD_PARTITIONING_SCHEME') == 'gpt' else ''}"

# Boot partition volume id
BOOTDD_VOLUME_ID ?= "boot_${MACHINE}"

UBOOT_REALSUFFIX_SDCARD ?= ".${UBOOT_SUFFIX_SDCARD}"
IMAGE_BOOTLOADER ?= "${@d.getVar('PREFERRED_PROVIDER_virtual/bootloader', True) or 'u-boot'}"
IMAGE_BOOTLOADER_RECIPE ?= "${IMAGE_BOOTLOADER}"

UBOOT_TYPE_SDCARD ?= "sdcard"
UBOOT_BASENAME_SDCARD ?= "u-boot"
UBOOT_NAME_SDCARD ?= "${UBOOT_BASENAME_SDCARD}-${MACHINE}${UBOOT_REALSUFFIX_SDCARD}-${UBOOT_TYPE_SDCARD}"
UBOOT_KERNEL_IMAGETYPE ?= "${KERNEL_IMAGETYPE}"
UBOOT_KERNEL_RECIPE ?= "virtual/kernel"
UBOOT_BOOTSPACE_SEEK ?= "2"

UBOOT_ENV_SDCARD_OFFSET ?= ""
UBOOT_ENV_SDCARD ?= "u-boot-environment"
UBOOT_ENV_NAME ??= "u-boot-flashenv-sd"
UBOOT_ENV_SDCARD_FILE ?= "${@d.getVar('UBOOT_ENV_NAME', True) and (d.getVar('UBOOT_ENV_NAME', True).split()[0] + '-${MACHINE}.bin') or ''}"

# The SDCARD_ROOTFS handling is easily broken. Due to the way images
# are created and how dependencies within the image creation process
# are handled, we need to ensure that we pull the rootfs from the
# IMGDEPLOYDIR and not from DEPLOY_DIR_IMAGE (which is the final user
# visible result). However, having this variable in the machine configs
# is ugly because it is an image process internal one. So we ignore
# the passed in path for compatibility and fix the reference.
# In a nutshell this also means that the whole concept of specifying
# a name in SDCARD_ROOTFS is broken because it has to match the
# IMAGE_NAME in the end anyway to leverage the internal dependency
# process as currently used. So the only thing of relevance is the
# extension and we can redo the rest internally. Oh well. *sigh*
# If we really wanted to use a different rootfs, we'd have to
# differentiate between the recipe name to provide the rootfs and the
# file resulting from that recipe with a given extension to get the
# dependencies right.
# To overcome this, we only take the extension from the SDCARD_ROOTFS
# now and build the right internal name from scratch.
# This permits us to build also with no card
# Another problem with the way the image classes work is that they
# add DATETIME into the IMAGE_NAME. If one subimage fails to build you
# can't just rerun the build to recreate the missing image from the
# dependencies. As the date changed then, the dependencies can no longer
# be found. As it appears this can't easily be fixed, the only solution
# then is to bitbake -c clean the image and rebuild it completely.
# In other words, All or Nothing.
SDCARD_ROOTFS_EXT ?= "${@d.getVar('SDCARD_ROOTFS', 1).split('.')[-1]}"
SDCARD_ROOTFS_REAL = "${@oe.utils.conditional("SDCARD_ROOTFS_EXT", "", "", "${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${SDCARD_ROOTFS_EXT}", d)}"
SDCARD_ROOTFS_PKG ?= "${PN}"

# For integration of raw flash like elements we fall back to the same
# variables as for the flash class. This permits using one set of
# variables in the machine definition mostly for different types of
# booting.
FLASHIMAGE_SIZE ??= "0"
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

# This should be a list of : <package>:<file> and the file
# should be deployed in ${DEPLOY_DIR_IMAGE}
SDCARDIMAGE_BOOT_EXTRA_FILES ?= ""

SDCARD_ROOTFS_EXTRA1_FILE ?= ""
SDCARD_ROOTFS_EXTRA1_SIZE ?= "0"
SDCARD_ROOTFS_EXTRA2_FILE ?= ""
SDCARD_ROOTFS_EXTRA2_SIZE ?= "0"

# Make the two extra rootfs partitions logical
SDCARD_ROOTFS_LOGICAL_EXTRA ?= "0"

TFA_IMAGE ?= ""
TFA_IMAGE_FILE ?= ""

TFA_RESET ?= ""
TFA_RESET_FILE ?= ""

SDCARD = "${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.sdcard"

# Set default alignment and padding to 4MB [in KiB]
INITIAL_PADDING_SIZE ?= "${IMAGE_ROOTFS_ALIGNMENT}"
ALIGNMENT_SIZE ?= "${BASE_IMAGE_ROOTFS_ALIGNMENT}"

SD_BL2_SIZE ?= "${EMMC_BL2_PARTITION_SIZE_KIB}"
SD_FAT_SIZE ?= "${BOOT_SPACE}"

def get_extra_files_fields(d, findex):
    pkgs = []
    extra_files = d.getVar('SDCARDIMAGE_BOOT_EXTRA_FILES', True)
    if not extra_files:
        return pkgs
    for f in extra_files.split():
        pair = f.split(":")
        if len(pair) == 2:
            pkgs.append(pair[findex])
    return pkgs

def get_pkgs_from_extra_files(d):
    return get_extra_files_fields(d, 0)

def get_files_from_extra_files(d):
    return get_extra_files_fields(d, 1)

def get_extra_pkgs_deploy(d):
    pkgs = get_pkgs_from_extra_files(d)
    print(pkgs)
    if pkgs:
        return ":do_deploy ".join(get_pkgs_from_extra_files(d)) + ":do_deploy"
    return ""

# We do not need to rely on HOSTTOOLS
do_image_sdcard[depends] += "mtools-native:do_populate_sysroot parted-native:do_populate_sysroot dosfstools-native:do_populate_sysroot"

# To support use of KERNEL_IMAGETYPE based access
do_image_sdcard[depends] += "${UBOOT_KERNEL_RECIPE}:do_deploy"
do_image_sdcard[depends] += " \
	${@d.getVar('SDCARD_RCW', True) and d.getVar('SDCARD_RCW', True) + ':do_deploy' or ''} \
	${@d.getVar('IMAGE_BOOTLOADER', True) and d.getVar('IMAGE_BOOTLOADER_RECIPE', True) + ':do_deploy' or ''} \
	${@d.getVar('INITRAMFS_IMAGE', True) and d.getVar('INITRAMFS_IMAGE', True) + ':do_image_complete' or ''} \
	${@d.getVar('SDCARD_ROOTFS_EXT', True) and d.getVar('SDCARD_ROOTFS_PKG', True) + ':do_image_${SDCARD_ROOTFS_EXT}' or ''} \
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
	${@get_extra_pkgs_deploy(d)} \
	${@d.getVar('TFA_IMAGE_FILE', True) and d.getVar('TFA_IMAGE', True) + ':do_deploy' or ''} \
	${@d.getVar('TFA_RESET_FILE', True) and d.getVar('TFA_RESET', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_ROOTFS_EXTRA1_FILE', True) and d.getVar('SDCARDIMAGE_ROOTFS_EXTRA1', True) + ':do_deploy' or ''} \
	${@d.getVar('SDCARDIMAGE_ROOTFS_EXTRA2_FILE', True) and d.getVar('SDCARDIMAGE_ROOTFS_EXTRA2', True) + ':do_deploy' or ''} \
"

generate_sdcardimage_entry_raw() {
	GSDE_IMAGE_FILE="$1"

	if [ -n "${GSDE_IMAGE_FILE}" ]; then
		bbnote "Generating sdcard entry at 0x$(printf "%x" $3) for ${GSDE_IMAGE_FILE}"
		rawimage_generate_entry "${GSDE_IMAGE_FILE}" "${SDCARD}" "$2" "$3" "$4" "$5"
	fi
}

generate_sdcardimage_entry() {
	file="$1"

	# For SD card, we don't flash firmwares inside the image.
	if echo "$file" | grep -q ".fw"; then
		return
	fi

	if [ -n "${file}" ]; then
		file="${DEPLOY_DIR_IMAGE}/${file}"

		if [ -z "$3" ]; then
			# Don't break the build. Just don't add the firmware to the image if the offset is not set.
			bbwarn "$2 is undefined. To use the 'sdcard' image it needs to be defined as byte offset."
			return
		fi
		generate_sdcardimage_entry_raw "${file}" "$2" "$3" "$4" "$5"
	fi
}

# Add extra images in the boot partition
add_extra_boot_img() {
	BOOT_IMAGE_FILE="$1"
	BOOT_IMAGE="$2"
	if [ -n "${BOOT_IMAGE_FILE}" ]; then
		mcopy -i "${BOOT_IMAGE}" -s "${DEPLOY_DIR_IMAGE}/${BOOT_IMAGE_FILE}" "::/${BOOT_IMAGE_FILE}"
	fi
}

# Format rootfs partition
create_rootfs_partition () {
	SDCARD_ROOTFS_START="$1"
	SDCARD_ROOTFS_SIZE="$2"
	SDCARD_ROOTFS_NAME="$3"
	SDCARD_ROOTFS_PART_TYPE="$4"
	if [ -n "${SDCARD_ROOTFS_NAME}" ]; then
		parted -s "${SDCARD}" unit KiB mkpart ${SDCARD_ROOTFS_PART_TYPE} ${SDCARD_ROOTFS_START} $(printf "%u + %u\n" ${SDCARD_ROOTFS_START} ${SDCARD_ROOTFS_SIZE} | bc)
	fi
}

# ROOTFS name contains the DATETIME string (timestamp). If both rootfs and
# sdcard are built, the file name used in sdcard is consistent with the real
# filename. However if only sdcard should be updated without re-building
# rootfs, there is timestamp inconsistency: timestamp at sdcard build time is
# different than rootfs build time.
# This can be easily reproduced when u-boot/atf/kernel are needed with no
# rootfs change.
# To fix this problem, this function reads the rootfs symbolic link that points
# to the correct rootfs file.
#
# Nothing is needed for dependency, this is already handled using:
# [vardepsexclude] += "DATETIME"
#
update_rootfs_name () {
	local img="$1"

	img=$(echo "${img}" | sed  "s#-[0-9]*${IMAGE_NAME_SUFFIX}.${SDCARD_ROOTFS_EXT}#.${SDCARD_ROOTFS_EXT}#g")
	img=$(readlink -f "${img}" || true)

	if [ -f "${img}" ]; then
		SDCARD_ROOTFS_NAME="${img}"
	else
		bberror "Rootfs file does not exist or invalid link: ${img}"
	fi
}


# Burn rootfs partition to .sdcard image
write_rootfs_partition () {
	SDCARD_ROOTFS_START="$1"
	SDCARD_ROOTFS_SIZE="$2"
	SDCARD_ROOTFS_NAME="$3"

	if [ -n "${SDCARD_ROOTFS_NAME}" -a ! -f "${SDCARD_ROOTFS_NAME}" ]; then
		# bitbake parsing doesn't like 'var="$(func <args>)"'
		# and optimizes func() away. So we can't return the
		# result when quoting properly. Net result is that we
		# modify the variable in the function directly
		update_rootfs_name ${SDCARD_ROOTFS_NAME}
	fi

	if [ -n "${SDCARD_ROOTFS_NAME}" ]; then
		generate_sdcardimage_entry_raw "${SDCARD_ROOTFS_NAME}" "" $(printf "%u * 1024\n" ${SDCARD_ROOTFS_START} | bc)
	fi
}

_create_fat_subdirs() {
	local subdir="$1"
	local current_path=""

	if [ -n "${subdir}" ]; then
		for dir in $(echo "${subdir}" | tr '/' ' '); do
			current_path="${current_path}/${dir}"
			mmd -i "${WORKDIR}/boot.img" "::${current_path}"
		done
	fi
}

#
# Generate the boot image with the boot scripts and required Device Tree
# files
_generate_boot_image() {
	local fat_part_idx=$1

	# Create boot partition image
	BOOT_BLOCKS=$(LC_ALL=C parted -s "${SDCARD}" unit b print \
		| awk "/ $fat_part_idx / { print substr(\$4, 1, length(\$4 -1)) / 1024 }")

	# mkdosfs will sometimes use FAT16 when it is not appropriate,
	# resulting in a boot failure from SYSLINUX. Use FAT32 for
	# images larger than 512MB, otherwise let mkdosfs decide.
	if [ $(printf "%u / 1024\n" $BOOT_BLOCKS | bc) -gt 512 ]; then
		FATSIZE="-F 32"
	fi

	rm -f "${WORKDIR}/boot.img"
	bvid="`echo ${BOOTDD_VOLUME_ID} | head -c 10`"
	mkfs.vfat -n "$bvid" -S 512 ${FATSIZE} -C "${WORKDIR}/boot.img" $BOOT_BLOCKS

	_create_fat_subdirs "${SD_FAT_BOOT_DIR}"
	_create_fat_subdirs "${SD_FAT_DTB_DIR}"

	FAT_BOOT_DEST="::/${SD_FAT_BOOT_DIR}/${UBOOT_KERNEL_IMAGETYPE}"

	mcopy -i "${WORKDIR}/boot.img" -s "${DEPLOY_DIR_IMAGE}/${UBOOT_KERNEL_IMAGETYPE}-${MACHINE}.bin" "${FAT_BOOT_DEST}"

	# Copy boot scripts
	for item in ${BOOT_SCRIPTS}; do
		src="`echo $item | awk -F':' '{ print $1 }'`"
		dst="`echo $item | awk -F':' '{ print $2 }'`"

		mcopy -i "${WORKDIR}/boot.img" -s "${DEPLOY_DIR_IMAGE}/$src" "::/$dst"
	done

	# Copy device tree file
	if test -n "${KERNEL_DEVICETREE}"; then
		kernel_bin="`readlink ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.bin`"
		for DTB_FILE in ${KERNEL_DEVICETREE}; do
			DTB_BASE_NAME="`basename ${DTB_FILE} | awk -F "." '{print $1}'`"
			DTB_PATH=""
			kernel_bin_for_dtb=""
			if [ -e "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${DTB_BASE_NAME}.dtb" ]; then
				DTB_PATH="${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${DTB_BASE_NAME}.dtb"
				kernel_bin_for_dtb="`readlink $DTB_PATH | sed "s,$DTB_BASE_NAME,${MACHINE},;s,\.dtb$,.bin,g"`"
			else if [ -e "${DEPLOY_DIR_IMAGE}/${DTB_BASE_NAME}.dtb" ]; then
					DTB_PATH="${DEPLOY_DIR_IMAGE}/${DTB_BASE_NAME}.dtb"
					kernel_bin_for_dtb="`readlink $DTB_PATH | sed "s,$DTB_BASE_NAME,${KERNEL_IMAGETYPE},;s,\.dtb$,.bin,g"`"

					# in case "${kernel_bin_for_dtb}" is empty, it means that $DTB_PATH is not a symlink, but the dtb file itself
					# this can happen in certain situations, like the default behavior starting 
					# with Yocto 4.3, if the default behavior from kernel-devicetree class is not overridden
					if [ -z "${kernel_bin_for_dtb}" ]; then
						kernel_bin_timestamp="`echo ${kernel_bin} | sed -e "s/^Image--//" -e "s/.bin//"`"
						kernel_bin_for_dtb="`find ${DEPLOY_DIR_IMAGE} -maxdepth 1 -type l -exec basename {} \; | grep "${kernel_bin_timestamp}.dtb" | sed "s,$DTB_BASE_NAME,${KERNEL_IMAGETYPE},;s,\.dtb$,.bin,g"`"
					fi
				fi
			fi
			if [ -n "${DTB_PATH}" ]; then
				# match dtb and kernel image by timestamp
				if [ "${kernel_bin}" = "${kernel_bin_for_dtb}" ]; then
					FAT_DTB_DEST="::/${SD_FAT_DTB_DIR}/${DTB_BASE_NAME}.dtb"
					mcopy -i "${WORKDIR}/boot.img" -s "${DTB_PATH}" "${FAT_DTB_DEST}"
				fi
			else
				bbfatal "${DTB_FILE} does not exist."
			fi
		done
	fi

	# Copy extlinux.conf to images that have U-Boot Extlinux support.
	if [ "${UBOOT_EXTLINUX}" = "1" ]; then
		mmd -i "${WORKDIR}/boot.img" ::/extlinux
		mcopy -i "${WORKDIR}/boot.img" -s "${DEPLOY_DIR_IMAGE}/extlinux.conf" ::/extlinux/extlinux.conf
	fi

	# Add extra boot images in the SDCARD boot partition
	for file in ${@" ".join(get_files_from_extra_files(d))}
	do
		add_extra_boot_img "${file}" "${WORKDIR}/boot.img"
	done
}

#
# A single function to burn the bootloader on any type of SD card
_burn_bootloader() {
	if [ "${SD_INCLUDE_BL2_PART}" = "1" ]; then
		_burn_bl2
	fi
	case "${IMAGE_BOOTLOADER}" in
		imx-bootlets)
		bberror "The imx-bootlets is not supported for i.MX based machines"
		exit 1
		;;
		u-boot*)
		if [ -n "${SPL_BINARY}" ]; then
				generate_sdcardimage_entry "${SPL_BINARY}" "" $(printf "%u" ${UBOOT_BOOTSPACE_OFFSET})
				generate_sdcardimage_entry "${UBOOT_NAME_SDCARD}" "" $(printf "%u + %u\n" ${UBOOT_BOOTSPACE_OFFSET} 0x11000 | bc)
		else
			if [ "${UBOOT_BOOTSPACE_OFFSET}" = "0" ]; then
				usize=$(stat -L -c "%s" "${DEPLOY_DIR_IMAGE}/${UBOOT_NAME_SDCARD}")
				# write IVT
				generate_sdcardimage_entry "${UBOOT_NAME_SDCARD}" "" 0 "bs=256 count=1" 256
				# write the rest of u-boot code
				usize=$(printf "%u - 512\n" ${usize} | bc)
				generate_sdcardimage_entry "${UBOOT_NAME_SDCARD}" "" 512 "iflag=skip_bytes skip=512" ${usize}
			else
				generate_sdcardimage_entry "${UBOOT_NAME_SDCARD}" "" $(printf "%u" ${UBOOT_BOOTSPACE_OFFSET})
			fi
		fi
		if [ -n "${UBOOT_ENV_SDCARD_OFFSET}" -a -n "${UBOOT_ENV_SDCARD_FILE}" ]; then
				generate_sdcardimage_entry "${UBOOT_ENV_SDCARD_FILE}" "" $(printf "%u" ${UBOOT_ENV_SDCARD_OFFSET})
		fi
		;;
		barebox)
		generate_sdcardimage_entry "barebox-${MACHINE}.bin" "" 512 "iflag=skip_bytes skip=512"
		generate_sdcardimage_entry "bareboxenv-${MACHINE}.bin" "" 0x00080000
		;;
		"")
		;;
		*)
		bberror "Unknown IMAGE_BOOTLOADER value"
		exit 1
		;;
	esac
}

_burn_bl2() {

	local bl2_offset=$(LC_ALL=C parted -s "${SDCARD}" unit b print \
		| awk '/ 1 / { print substr($2, 1, length($2)-1) }')

	local bl2_size=$(stat -L -c "%s" "${SD_BL2_PATH}")
	generate_sdcardimage_entry "${BL2_NAME_SDCARD}" "" ${bl2_offset} "" ${bl2_size}
}

_burn_fip() {
	local fip_part="$1"

	local fip_offset=$(LC_ALL=C parted -s "${SDCARD}" unit b print \
		| awk "/ $fip_part / { print substr(\$2, 1, length(\$2 -1)) }")

	generate_sdcardimage_entry "${FIRMWARE_NAME_SDCARD}" "" ${fip_offset} "" ${SD_FIP_SIZE}
}

# External variables needed:
#   ${SDCARD_ROOTFS}    - the rootfs image type to incorporate, e.g., ext3
#   ${SDCARD_ROOTFS_EXTRA1_*} - Optional additional partition definition
#   ${SDCARD_RCW_NAME}  - RCW for Layerscape devices
#   ${IMAGE_BOOTLOADER} - bootloader to use {u-boot, barebox}
#
# Without the BL2 dedicated partition, the disk layout used is:
#
#    0                                                  -> INITIAL_PADDING SIZE                                             - reserved to bootloader (not partitioned)
#    INITIAL_PADDING_SIZE                               -> INITIAL_PADDING SIZE + SD_FIP_SIZE                               - reserved to FIP
#    INITIAL_PADDING SIZE + SD_FIP_SIZE                 -> INITIAL_PADDING SIZE + SD_FIP_SIZE + SD_FAT_SIZE                 - kernel and other data
#    INITIAL_PADDING SIZE + SD_FIP_SIZE + SD_FAT_SIZE   -> INITIAL_PADDING SIZE + SD_FIP_SIZE + SD_FAT_SIZE + ROOTFS_SIZE   - rootfs
#
# The numerical values used in the visual representation below are illustrative examples based on current default values.
#
#                                                       Default Free space = 1.3x
#                                                       Use IMAGE_OVERHEAD_FACTOR to add more space
#                                                       <--------->                                              (optional)
#            4MiB                 4MiB             8MiB            SDIMG_ROOTFS0                    4MiB                          SDIMG_ROOTFSn                   4MiB
# <-----------------------> <--------------> <--------------> <----------------------> <----------------------------->       <----------------------> <----------------------------->
#  ------------------------ ---------------- ---------------- -----------------------  -------------------------------  ....  ----------------------- ------------------------------
# |  INITIAL_PADDING_SIZE  |   SD_FIP_SIZE  |   SD_FAT_SIZE  |      ROOTFS_SIZE       |         ALIGNMENT_SIZE         |    |      ROOTFS_SIZE       |        ALIGNMENT_SIZE        |
#  ------------------------ ---------------- ---------------- ----------------------- -------------------------------   ....  ----------------------- ------------------------------
# ^                        ^                ^                ^                        ^                                      ^                                                      ^
# |                        |                |                |                        |                                      |                                                      |
# 0                      4096          4MiB + 4MiB   4MiB + 4MiB + 8MiB   4MiB + 4MiB + 8Mib + SDIMG_ROOTFS   16MiB + (SDIMG_ROOTFS + 4MiB) * n                 16MiB + (SDIMG_ROOTFS + 4MiB) * (n + 1)
#                 
#                                                            |                                                       |       |                                                      |
#                                                            |                                                       |       |                                                      |
#                                                             <----------------------------------------------------->  .....  <---------------------------------------------------->
#                                                                                        |                                                              |
#                                                                                     ROOTFS0                                                        ROOTFSn
# With the BL2 dedicated partition, the disk layout used is:
#
#    0                                                              -> INITIAL_PADDING SIZE                                                         - reserved to bootloader (not partitioned)
#    INITIAL_PADDING_SIZE                                           -> INITIAL_PADDING_SIZE + SD_BL2_SIZE                                           - reserved to BL2
#    INITIAL_PADDING_SIZE + SD_BL2_SIZE                             -> INITIAL_PADDING_SIZE + SD_BL2_SIZE + SD_FIP_SIZE                             - reserved to FIP
#    INITIAL_PADDING_SIZE + SD_BL2_SIZE + SD_FIP_SIZE               -> INITIAL_PADDING_SIZE + SD_BL2_SIZE + SD_FIP_SIZE + SD_FAT_SIZE               - kernel and other data
#    NITIAL_PADDING_SIZE + SD_BL2_SIZE + SD_FIP_SIZE + SD_FAT_SIZE  -> NITIAL_PADDING_SIZE + SD_BL2_SIZE + SD_FIP_SIZE + SD_FAT_SIZE + ROOTFS_SIZE  - rootfs
#
# The numerical values used in the visual representation below are illustrative examples based on current default values.
#
#                                                             		Default Free space = 1.3x
#                                                             		Use IMAGE_OVERHEAD_FACTOR to add more space
#                                                             		<--------->                                              (optional)
#          1Mib                   512K                 1MiB                  8MiB            SDIMG_ROOTFS0                1MiB                        SDIMG_ROOTFSn                1Mib
# <-----------------------> <--------------> <-----------------------> <--------------> <----------------------> <----------------------->  ...  <----------------------> <----------------------->
# |  INITIAL_PADDING_SIZE  |   SD_BL2_SIZE  |       SD_FIP_SIZE       |   SD_FAT_SIZE  |     ROOTFS_SIZE        |      ALIGNMENT_SIZE      |...|     ROOTFS_SIZE         |      ALIGNMENT_SIZE    |
# ^                        ^                ^                         ^                ^                        ^                                                        ^                        ^
# |                        |                |                         |                |                        |                                                        |                        |
# 0                      1024       1MiB + 512K      1MiB + 512K + 1MiB 1MiB + 512K + 1MiB + 8MiB 1MiB + 512K + 1MiB + 8MiB + SDIMG_ROOTFS|      |          9.5MiB + (SDIMG_ROOTFS + 1MiB) * n    9.5MiB + (SDIMG_ROOTFS + 1MiB) * (n + 1)
#
#                                                                                      |                                                  |      |                                                |
#                                                                                      |                                                  |      |                                                |
#                                                                                       <-----------------------------------------------> |  ... | <---------------------------------------------->
#                                                                                                          |                                                              |
#                                                                                                       ROOTFS0                                                        ROOTFSn
generate_nxp_sdcard () {
	# Get partitions' start offsets passed as parameters
	SDCARD_ROOTFS_REAL_START="$1"
	SDCARD_ROOTFS_EXTRA1_START="$2"
	SDCARD_ROOTFS_EXTRA2_START="$3"
	SDCARD_EXTENDED_PART_START="$4"

	# Create partition table
	export PARTED_SECTOR_SIZE=${SDCARD_SECTOR_SIZE}
	parted -s "${SDCARD}" mklabel "${SDCARD_PARTITIONING_SCHEME}"

	if [ ${SD_BL2_SIZE_ALIGNED} -gt 0 ]; then
		parted -s "${SDCARD}" unit KiB mkpart ${SDCARD_BL2_PARTITION_NAME} \
										${INITIAL_PADDING_SIZE} \
										$(printf "%u + %u\n" ${INITIAL_PADDING_SIZE} ${SD_BL2_SIZE_ALIGNED} | bc)
	fi

	if [ ${SD_FIP_SIZE_ALIGNED} -gt 0 ]; then
		parted -s "${SDCARD}" unit KiB mkpart "${SDCARD_FIP_PARTITION_NAME}" \
										${SD_FIP_START_OFFSET} \
										$(printf "%u + %u\n" ${SD_FIP_START_OFFSET} ${SD_FIP_SIZE_ALIGNED} | bc)
	fi

	if [ ${SD_FAT_SIZE_ALIGNED} -gt 0 ]; then
		parted -s "${SDCARD}" unit KiB mkpart "${SDCARD_FAT_PARTITION_NAME}" \
										fat32 \
										${SD_FAT_START_OFFSET} \
										$(printf "%u + %u\n" ${SD_FAT_START_OFFSET} ${SD_FAT_SIZE_ALIGNED} | bc)
	fi

	create_rootfs_partition ${SDCARD_ROOTFS_REAL_START} ${ROOTFS_SIZE} "${SDCARD_ROOTFS_REAL}" "${SDCARD_ROOTFS_PARTITION_NAME}"

	if [ ${SDCARD_ROOTFS_EXTRA1_START} -ne 0 ] && [ ${SDCARD_ROOTFS_EXTRA2_START} -ne 0 ] && [ ${SDCARD_ROOTFS_LOGICAL_EXTRA} -eq 0 ]; then
		bberror "Need to select SDCARD_ROOTFS_LOGICAL_EXTRA=1 when having two extra rootfs partitions"
	fi

	if [ ${SDCARD_ROOTFS_LOGICAL_EXTRA} -eq 1 ]; then
		rootfs_extra_part_type="logical"
		parted -s "${SDCARD}" unit KiB mkpart extended ${SDCARD_EXTENDED_PART_START} 100%
	else
		rootfs_extra_part_type="primary"
	fi
	create_rootfs_partition ${SDCARD_ROOTFS_EXTRA1_START} ${SDCARD_ROOTFS_EXTRA1_SIZE} "${SDCARD_ROOTFS_EXTRA1_FILE}" "${rootfs_extra_part_type}"
	create_rootfs_partition ${SDCARD_ROOTFS_EXTRA2_START} ${SDCARD_ROOTFS_EXTRA2_SIZE} "${SDCARD_ROOTFS_EXTRA2_FILE}" "${rootfs_extra_part_type}"
	parted "${SDCARD}" print

	# Fill optional Layerscape RCW into the boot block
	if [ -n "${SDCARD_RCW_NAME}" ]; then
		generate_sdcardimage_entry "${SDCARD_RCW_NAME}" "" 0x00001000
	fi

	_burn_bootloader

	if [ ${SD_FIP_SIZE_ALIGNED} -gt 0 ]; then
		if [ -n "${SD_FIP_BURN_PART_INDEX}" ]; then
			_burn_fip ${SD_FIP_BURN_PART_INDEX}
		fi
    fi

	# Burn Partitions
	if [ ${SD_FIP_SIZE_ALIGNED} -gt 0 ]; then
		# If the dedicated partition for BL2 is added, the boot partition will now be the 3rd one
		if [ ${SD_BL2_SIZE_ALIGNED} -gt 0 ]; then
			FAT_PART_IDX=3
		else
			FAT_PART_IDX=2
		fi
	else
		FAT_PART_IDX=1
	fi
	_generate_boot_image ${FAT_PART_IDX}
	generate_sdcardimage_entry_raw "${WORKDIR}/boot.img" "" $(printf "%u * 1024\n" ${SD_FAT_START_OFFSET} | bc)

	write_rootfs_partition ${SDCARD_ROOTFS_REAL_START} ${ROOTFS_SIZE} "${SDCARD_ROOTFS_REAL}"
	write_rootfs_partition ${SDCARD_ROOTFS_EXTRA1_START} ${SDCARD_ROOTFS_EXTRA1_SIZE} "${SDCARD_ROOTFS_EXTRA1_FILE}"
	write_rootfs_partition ${SDCARD_ROOTFS_EXTRA2_START} ${SDCARD_ROOTFS_EXTRA2_SIZE} "${SDCARD_ROOTFS_EXTRA2_FILE}"
}

IMAGE_CMD:sdcard () {

	local sd_size="0"
	SD_FIP_SIZE=$(du -k "${SD_FIP_PATH}" | cut -f1)

	if [ -n "${UBOOT_BOOTSPACE_OFFSET}" ]; then
		UBOOT_BOOTSPACE_OFFSET=$(printf "%u" ${UBOOT_BOOTSPACE_OFFSET})
	else
		UBOOT_BOOTSPACE_OFFSET=$(printf "%u * 512\n" ${UBOOT_BOOTSPACE_SEEK} | bc)
	fi

	# Align bootloader, firmware & boot partitions and calculate total SD card image size
	# Their corresponding partitions will not be created if either SD_BL2_SIZE, SD_FAT_SIZE or SD_FIP_SIZE is 0 .
	SD_BL2_SIZE_ALIGNED=$(printf "s=%u;a=%u;x=s+a-1;x-x%%a\n" ${SD_BL2_SIZE} ${ALIGNMENT_SIZE} | bc)
	SD_FIP_SIZE_ALIGNED=$(printf "s=%u;a=%u;x=s+a-1;x-x%%a\n" ${SD_FIP_SIZE} ${ALIGNMENT_SIZE} | bc)
	SD_FAT_SIZE_ALIGNED=$(printf "s=%u;a=%u;x=s+a-1;x-x%%a\n" ${SD_FAT_SIZE} ${ALIGNMENT_SIZE} | bc)

	# If the size has not been preset, we default to flash image
	# sizes if available turned into [KiB] or to a hardcoded minimum
	# default of 4MB.
	if [ -z "${INITIAL_PADDING_SIZE}" ]; then
		if [ -n "${FLASHIMAGE_SIZE}" ]; then
			INITIAL_PADDING_SIZE=$(printf "%u * 1024\n" ${FLASHIMAGE_SIZE} | bc)
		else
			INITIAL_PADDING_SIZE=${ALIGNMENT_SIZE}
		fi
	fi

	# Compute final size of SDCard image and start offset of each rootfs partition
	SD_FIP_START_OFFSET=$(printf "%u + %u\n" ${INITIAL_PADDING_SIZE} ${SD_FW_SIZE_ALIGNED} | bc)
	SD_FAT_START_OFFSET=$(printf "%u + %u\n" ${SD_FIP_START_OFFSET} ${SD_FIP_SIZE_ALIGNED} | bc)
	sd_size=$(printf "%u + %u\n" ${SD_FAT_START_OFFSET} ${SD_FAT_SIZE_ALIGNED} | bc)

	if [ -n "${SDCARD_ROOTFS_REAL}" ]; then
		SDCARD_ROOTFS_REAL_START=${sd_size}
		sd_size=$(printf "%u + %u + %u\n" ${sd_size} ${ROOTFS_SIZE} ${ALIGNMENT_SIZE} | bc)
	else
		SDCARD_ROOTFS_REAL_START="0"
	fi

	if [ ${SDCARD_ROOTFS_LOGICAL_EXTRA} -eq 1 ]; then
		SDCARD_EXTENDED_PART_START=${sd_size}
		sd_size=$(printf "%u + %u\n" ${sd_size} ${ALIGNMENT_SIZE} | bc)
	else
		SDCARD_EXTENDED_PART_START="0"
	fi

	if [ -n "${SDCARD_ROOTFS_EXTRA1_FILE}" ]; then
		SDCARD_ROOTFS_EXTRA1_START=${sd_size}
		sd_size=$(printf "%u + %u + %u\n"  ${sd_size} ${SDCARD_ROOTFS_EXTRA1_SIZE} ${ALIGNMENT_SIZE} | bc)
	else
		SDCARD_ROOTFS_EXTRA1_START="0"
	fi
	if [ -n "${SDCARD_ROOTFS_EXTRA2_FILE}" ]; then
		SDCARD_ROOTFS_EXTRA2_START=${sd_size}
		sd_size=$(printf "%u + %u + %u\n"  ${sd_size} ${SDCARD_ROOTFS_EXTRA2_SIZE} ${ALIGNMENT_SIZE} | bc)
	else
		SDCARD_ROOTFS_EXTRA2_START="0"
	fi

	cd "${IMGDEPLOYDIR}"

	# Initialize a sparse file
	SDIMAGE_SIZE_D=$(printf "%u * 1024\n" ${sd_size} | bc)
	dd if=/dev/zero "of=${SDCARD}" bs=1 count=0 oflag=seek_bytes seek=${SDIMAGE_SIZE_D}

	rawimage_initutils "SD card/eMMC image" "no" ${SDIMAGE_SIZE_D};

	# Additional elements for the raw image, copying the approach of the flashimage class
	generate_sdcardimage_entry "${SDCARDIMAGE_EXTRA1_FILE}" "SDCARDIMAGE_EXTRA1_OFFSET" "${SDCARDIMAGE_EXTRA1_OFFSET}"
	generate_sdcardimage_entry "${SDCARDIMAGE_EXTRA2_FILE}" "SDCARDIMAGE_EXTRA2_OFFSET" "${SDCARDIMAGE_EXTRA2_OFFSET}"
	generate_sdcardimage_entry "${SDCARDIMAGE_EXTRA3_FILE}" "SDCARDIMAGE_EXTRA3_OFFSET" "${SDCARDIMAGE_EXTRA3_OFFSET}"
	generate_sdcardimage_entry "${SDCARDIMAGE_EXTRA4_FILE}" "SDCARDIMAGE_EXTRA4_OFFSET" "${SDCARDIMAGE_EXTRA4_OFFSET}"
	generate_sdcardimage_entry "${SDCARDIMAGE_EXTRA5_FILE}" "SDCARDIMAGE_EXTRA5_OFFSET" "${SDCARDIMAGE_EXTRA5_OFFSET}"
	generate_sdcardimage_entry "${SDCARDIMAGE_EXTRA6_FILE}" "SDCARDIMAGE_EXTRA6_OFFSET" "${SDCARDIMAGE_EXTRA6_OFFSET}"
	generate_sdcardimage_entry "${SDCARDIMAGE_EXTRA7_FILE}" "SDCARDIMAGE_EXTRA7_OFFSET" "${SDCARDIMAGE_EXTRA7_OFFSET}"
	generate_sdcardimage_entry "${SDCARDIMAGE_EXTRA8_FILE}" "SDCARDIMAGE_EXTRA8_OFFSET" "${SDCARDIMAGE_EXTRA8_OFFSET}"
	generate_sdcardimage_entry "${SDCARDIMAGE_EXTRA9_FILE}" "SDCARDIMAGE_EXTRA9_OFFSET" "${SDCARDIMAGE_EXTRA9_OFFSET}"

	generate_nxp_sdcard ${SDCARD_ROOTFS_REAL_START} ${SDCARD_ROOTFS_EXTRA1_START} ${SDCARD_ROOTFS_EXTRA2_START} ${SDCARD_EXTENDED_PART_START}
	cd -

	if [ "${SDCARD_PARTITIONING_SCHEME}" = "gpt" ]; then
		sgdisk -e "${SDCARD}"
	fi
}
