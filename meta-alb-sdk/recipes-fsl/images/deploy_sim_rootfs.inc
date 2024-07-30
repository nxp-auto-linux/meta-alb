# Deploy TF-A
DEPENDS:append = " arm-trusted-firmware"

inherit deploy

do_deploy() {
	rootfs_deploy_dir="${DEPLOYDIR}/simimage/rootfs"
	install -d "${rootfs_deploy_dir}"
	cp -vfL "${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.cpio" "${rootfs_deploy_dir}/"

	install -d "${DEPLOYDIR}/ufsimage"
	cp -vfL "${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.ext4" "${DEPLOYDIR}/ufsimage"
}

addtask deploy before do_image_complete after do_image_cpio do_image_ext4

