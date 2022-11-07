#
# This is a generic recipe to generate an .itb image containing
# just the kernel and dtb to install as boot image into /boot
# Technically, this could be part of the standard kernel build
# mechanisms, but hacking them in a layer is more complicated
# than providing a generic recipe.
#
DESCRIPTION = "Linux kernel ITB generation"
SECTION = "kernel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

# Include a fsl-image-core as fallback ramdisk.
# This helps debugging device trees for a full rootfs and kernel
ITB_ROOTFS_TYPE = "ext2.gz"
ITB_ROOTFS_NAME_EXT = "-withrootfs"
FALLBACK_RAMDISK = "fsl-image-itbflash"
ITB_ROOTFS_BASENAME ?= "${FALLBACK_RAMDISK}"
DEPENDS = "${FALLBACK_RAMDISK}"
DEPENDS += "dtc-native"
do_install[depends] = "${FALLBACK_RAMDISK}:do_image_complete"

require linux-kernelitb.inc
