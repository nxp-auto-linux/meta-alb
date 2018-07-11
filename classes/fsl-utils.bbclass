
IMAGE_ROOTFS_DEP_EXT ??= "ext2.gz"

rootfs_copy_core_image() {
    mkdir -p ${IMAGE_ROOTFS}/boot
    cp ${DEPLOY_DIR_IMAGE}/fsl-image-networking-${MACHINE}.${IMAGE_ROOTFS_DEP_EXT} ${IMAGE_ROOTFS}/boot/
}
