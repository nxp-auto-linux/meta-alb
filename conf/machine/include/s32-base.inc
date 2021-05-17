require conf/machine/include/soc-family.inc
require conf/machine/include/arm/arch-arm64.inc

SOC_FAMILY = "s32"

# Provides the common settings for S32 targets
# Machines may include this file directly, if little to no changes are
# required for their definitions.
# Otherwise, intermediate include files should be
# created for various families (e.g. s32v-base.inc, s32g-base.inc etc)

KERNEL_IMAGETYPE ?= "Image"

BOOT_SPACE ?= "65536"

# required for u-boot
HOSTTOOLS += "comm"

# required for sdcard deploy
HOSTTOOLS += "parted mkfs.vfat mcopy"

# S32 BSP default providers
PREFERRED_PROVIDER_u-boot ?= "u-boot-s32"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-s32"
PREFERRED_PROVIDER_virtual/kernel = "linux-s32"

PREFERRED_VERSION_linux-s32 ?= "5.10.41"
PREFERRED_VERSION_u-boot-s32 ?= "2020.04"
PREFERRED_VERSION_xen ?= "4.14"

PREFERRED_VERSION_rng-tools ?= "5"
PREFERRED_VERSION_tcpreplay ?= "4.3.3"

# ensure bison is not removed from sysroot, otherwise
# some build tasks e.g. kernel 'do_merge_delta_config' will fail
SSTATE_EXCLUDEDEPS_SYSROOT_remove = ".*->bison-native"

UBOOT_BOOTSPACE_SEEK ?= "8"
UBOOT_KERNEL_IMAGETYPE ?= "Image"
UBOOT_ENTRYPOINT ?= "80080000"
# Comment next line to prevent writing the environment to sdcard
UBOOT_ENV_SDCARD_OFFSET ??= "0x00100000"

#s32 has no hardware clock
MACHINE_FEATURES_BACKFILL_CONSIDERED = "rtc"

# we do not want to have getty running on tty1 as we run
# auto-serial-console there USE_VT = "0"
SERIAL_CONSOLES ?= "115200;ttyLF0"

SOC_DEFAULT_IMAGE_FSTYPES = "tar.gz"
IMAGE_FSTYPES ?= "${SOC_DEFAULT_IMAGE_FSTYPES}"

MACHINE_FEATURES ?= "pci ext2 ext4 serial"
MACHINE_EXTRA_RRECOMMENDS += "udev-extraconf kernel-modules"

EXTRA_IMAGEDEPENDS += "u-boot-s32 "

# Sub-architecture support
MACHINE_SOCARCH_SUFFIX ?= ""
MACHINE_SOCARCH_SUFFIX_s32 = "-s32"

MACHINE_ARCH_FILTER = "virtual/kernel"

INHERIT += "fsl-dynamic-packagearch"

# Mask some recipes which address other architectures, and
# which reside in layers that we may not use for S32
BBMASK = "u-boot-qoriq linux-qoriq"

# Xen Examples support variables
DOM0LESS_ROOTFS ?= "fsl-image-dom0less"
XEN_EXAMPLE ?= "xen-examples-default"
