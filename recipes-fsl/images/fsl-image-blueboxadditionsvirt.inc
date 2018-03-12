# This is similar to fsl-image-virt but requires the image to be
# including this file and also crosslayer-rootfs-full.inc

# copy core rootfs image as default image for virtual machines.
inherit fsl-rootfsimage
do_rootfs[depends] += "${IMAGE_ROOTFS_DEP}:do_rootfs"
IMAGE_ROOTFS_IMAGELIST = "${IMAGE_ROOTFS_DEP}-${MACHINE}.${IMAGE_ROOTFS_DEP_EXT}"

VIRT_PACKAGES = " \
    libvirt \
    libvirt-libvirtd \
    libvirt-virsh \
    lxc \
"

IMAGE_INSTALL += "${@bb.utils.contains("BBFILE_COLLECTIONS", "virtualization-layer", "${VIRT_PACKAGES}", "", d)}"

IMAGE_INSTALL += " \
    qemu \
"
