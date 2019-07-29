require conf/machine/include/s32-base.inc

IMAGE_FSTYPES += "cpio.gz"
IMAGE_FSTYPES_remove = "flashimage"

KERNEL_DEFCONFIG ?= "s32gen1_defconfig"

UBOOT_CONFIG = "cpio"
UBOOT_SUFFIX ?= "bin"

VIRTUAL-RUNTIME_dev_manager = "busybox-mdev"
VIRTUAL-RUNTIME_login_manager = "busybox"
VIRTUAL-RUNTIME_init_manager = "busybox"
VIRTUAL-RUNTIME_initscripts = "initscripts"
VIRTUAL-RUNTIME_keymaps = "keymaps"
DISTRO_FEATURES_BACKFILL_CONSIDERED += "sysvinit"
DISTRO_FEATURES_append += " virtio "

PREFERRED_VERSION_gmac = "${@bb.utils.contains('PREFERRED_VERSION_linux-s32', '4.14', '0.4.1', '0.4.2', d)}"
# For kernel 4.14 we have a separate gmac recipe; for kernel 4.19, gmac is built with the kernel
GMAC_IMAGE_INSTALL = "${@bb.utils.contains('PREFERRED_VERSION_linux-s32', '4.14', 'gmac', '', d)}"

# Add gmac by default
DISTRO_FEATURES_append += "${@bb.utils.contains('PREFERRED_VERSION_linux-s32', '4.14', ' gmac', '', d)}"

SOC_FAMILY_append = ":gen1"
