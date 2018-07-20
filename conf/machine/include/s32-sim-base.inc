require conf/machine/include/s32-base.inc

IMAGE_FSTYPES += "cpio.gz"

KERNEL_DEFCONFIG ?= "s32gen1_defconfig"

UBOOT_CONFIG = "cpio"
UBOOT_SUFFIX ?= "bin"

VIRTUAL-RUNTIME_dev_manager = "busybox-mdev"
VIRTUAL-RUNTIME_login_manager = "busybox"
VIRTUAL-RUNTIME_init_manager = "busybox"
VIRTUAL-RUNTIME_initscripts = "initscripts"
VIRTUAL-RUNTIME_keymaps = "keymaps"
DISTRO_FEATURES_BACKFILL_CONSIDERED += "sysvinit"

SOC_FAMILY_append = ":gen1"
