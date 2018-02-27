# This mini class helps copying full rootfs images of any kind into the
# rootfs of the current image. For obvious reasons, recursion is not
# supported. Due to the delta in file and recipe names, this class
# will not be able to establish the dependencies, so the image that
# includes the class will have to to do the proper
# 	do_roots[depends] +=
# for the image files copied here.

ROOTFS_POSTPROCESS_COMMAND += "rootfs_copy_imagefiles;"

rootfs_copy_imagefiles() {
    mkdir -p ${IMAGE_ROOTFS}/boot
    for name in ${IMAGE_ROOTFS_IMAGELIST}; do
	    cp ${DEPLOY_DIR_IMAGE}/$name ${IMAGE_ROOTFS}/boot/
    done
}
