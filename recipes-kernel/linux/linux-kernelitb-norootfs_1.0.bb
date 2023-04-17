#
# This is a generic recipe to generate an .itb image containing
# just the kernel and dtb to install as boot image into /boot
# Technically, this could be part of the standard kernel build
# mechanisms, but hacking them in a layer is more complicated
# than providing a generic recipe.
#
DESCRIPTION = "Linux kernel ITB generation without rootfs"
SECTION = "kernel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

# Default: No rootfs to be included in the itb
# No ramdisk is required for any boot of a full rootfs
ITB_ROOTFS_TYPE = ""
ITB_ROOTFS_NAME_EXT = ""

# We use the normal full kernel on this one as we do not have
# to mix with a rootfs
FLASHIMAGE_KERNEL = ""
FLASHIMAGE_KERNEL_BASENAME = ""

require linux-kernelitb.inc
DEPENDS += "dtc-native"
