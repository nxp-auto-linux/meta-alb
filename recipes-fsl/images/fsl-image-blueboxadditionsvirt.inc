# This is similar to fsl-image-virt but requires the image to be
# including this file.

# copy core rootfs image as default image for virtual machines.
inherit fsl-rootfsimage
do_rootfs[depends] += "fsl-image-networking:do_rootfs"
IMAGE_ROOTFS_IMAGELIST = "fsl-image-networking-${MACHINE}.ext2.gz"

IMAGE_INSTALL += " \
    libvirt \
    libvirt-libvirtd \
    libvirt-virsh \
    lxc \
    qemu"
