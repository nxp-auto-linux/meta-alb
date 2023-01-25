require conf/machine/include/s32-base.inc

IMAGE_FSTYPES += "cpio.gz"
IMAGE_FSTYPES:remove = "flashimage"

KERNEL_DEFCONFIG ?= "s32cc_defconfig"

UBOOT_CONFIG = "cpio"

VIRTUAL-RUNTIME_dev_manager = "busybox-mdev"
VIRTUAL-RUNTIME_login_manager = "busybox"
VIRTUAL-RUNTIME_init_manager = "busybox"
VIRTUAL-RUNTIME_initscripts = "initscripts"
VIRTUAL-RUNTIME_keymaps = "keymaps"
DISTRO_FEATURES:append = " virtio"
DISTRO_FEATURES:remove = "systemd"

SOC_FAMILY:append = ":s32cc"
