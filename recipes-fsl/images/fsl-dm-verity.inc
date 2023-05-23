IMAGE_FSTYPES = "wic tar.gz ext3"

COMPATIBLE_MACHINE = "s32cc"

do_make_wic() {

	if [ "${IMAGE_BOOTLOADER}" = "u-boot-s32" ]; then
		cd ${DEPLOY_DIR_IMAGE}

		dd if=${UBOOT_NAME_SDCARD} of=${IMAGE_LINK_NAME}.wic conv=notrunc bs=256 count=1 seek=0
		dd if=${UBOOT_NAME_SDCARD} of=${IMAGE_LINK_NAME}.wic conv=notrunc bs=512 seek=1 skip=1
	else
		bberror "IMAGE_BOOTLOADER value not compatible with dm-verity current support"
		exit 1
	fi
}

addtask make_wic after do_image_complete before do_build

do_make_wic[depends] += " \
	${@d.getVar('ATF_IMAGE_FILE', True) and d.getVar('ATF_IMAGE', True) + ':do_deploy' or ''} \
	${@d.getVar('IMAGE_BOOTLOADER', True) and d.getVar('IMAGE_BOOTLOADER_RECIPE', True) + ':do_deploy' or ''} \
"
