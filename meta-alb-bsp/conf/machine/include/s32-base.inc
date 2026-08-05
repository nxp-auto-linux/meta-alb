require conf/machine/include/soc-family.inc

SOC_FAMILY = "s32"

# Provides the common settings for S32 targets
# Machines may include this file directly, if little to no changes are
# required for their definitions.
# Otherwise, intermediate include files should be
# created for various families (e.g. s32g-base.inc etc)

KERNEL_IMAGETYPE ?= "Image"

BOOT_SPACE ?= "65536"

# required for u-boot
HOSTTOOLS += "comm"

# S32 BSP default providers
PREFERRED_PROVIDER_u-boot ?= "u-boot-s32"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-s32"
PREFERRED_PROVIDER_virtual/kernel ?= "linux-s32"

PREFERRED_VERSION_linux-s32 ?= "6.12%"
PREFERRED_VERSION_u-boot-s32 ?= "2022.04"
PREFERRED_VERSION_u-boot-tools ?= "2022.04"
PREFERRED_VERSION_u-boot-tools-native ?= "2022.04"
PREFERRED_VERSION_nativesdk-u-boot-tools ?= "2022.04"
PREFERRED_VERSION_xen ?= "4.21"
PREFERRED_VERSION_xen-tools ?= "4.21"

PREFERRED_VERSION_optee-os ?= "4.0.0"
PREFERRED_VERSION_optee-client ?= "4.0.0"
PREFERRED_VERSION_optee-test ?= "4.0.0"
PREFERRED_VERSION_optee-examples ?= "4.0.0"

PREFERRED_VERSION_arm-trusted-firmware ?= "2.10.25"
PREFERRED_VERSION_arm-trusted-firmware-tools ?= "2.10.25"

PREFERRED_VERSION_rng-tools ?= "5"
PREFERRED_VERSION_tcpreplay ?= "4.4.4"
PREFERRED_VERSION_qemu ?= "8.2.7"

# ensure bison is not removed from sysroot, otherwise
# some build tasks e.g. kernel 'do_merge_delta_config' will fail
SSTATE_EXCLUDEDEPS_SYSROOT:remove = ".*->bison-native"

UBOOT_BOOTSPACE_SEEK ?= "8"
UBOOT_KERNEL_IMAGETYPE ?= "Image"
UBOOT_ENTRYPOINT ?= "80080000"
# Comment next line to prevent writing the environment to sdcard
UBOOT_ENV_SDCARD_OFFSET ??= "0x00100000"

# Used when TF-A and Linux have different naming convention for the DTB file
KERNEL_DEVICETREE_OVERRIDE ?= ""

# Used for out-of-tree Linux DTB
KERNEL_DEVICETREE_EXT ?= ""

TFA_DEVICETREE = "${@os.path.basename(d.getVar('KERNEL_DEVICETREE').split()[0] \
		if d.getVar('KERNEL_DEVICETREE_OVERRIDE') == '' \
		else d.getVar('KERNEL_DEVICETREE_OVERRIDE'))}"

#s32 has no hardware clock
MACHINE_FEATURES_BACKFILL_CONSIDERED = "rtc"

SOC_DEFAULT_IMAGE_FSTYPES = "tar.gz"
IMAGE_FSTYPES ?= "${SOC_DEFAULT_IMAGE_FSTYPES}"

MACHINE_FEATURES ?= "pci ext2 ext4 serial"

EXTRA_IMAGEDEPENDS += "u-boot-s32 "

# Sub-architecture support
MACHINE_SOCARCH_SUFFIX ?= ""
MACHINE_SOCARCH_SUFFIX:s32 = "-s32"

MACHINE_ARCH_FILTER = "virtual/kernel"

INHERIT += "fsl-dynamic-packagearch"

# Mask some recipes which address other architectures, and
# which reside in layers that we may not use for S32
BBMASK += "u-boot-qoriq linux-qoriq"

# Xen Examples support variables
DOM0LESS_ROOTFS ?= "fsl-image-domu"
XEN_EXAMPLE ?= "xen-examples-default"

# Xen Rootfs Partitions support
SDCARD_ROOTFS_EXTRA1_FILE ?= "${@bb.utils.contains('DISTRO_FEATURES', 'xen', '${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${SDCARD_ROOTFS_EXT}', '', d)}"
SDCARD_ROOTFS_EXTRA1_SIZE ?= "${@bb.utils.contains('DISTRO_FEATURES', 'xen', '${ROOTFS_SIZE}', '0', d)}"

# Disable NFS server service
DISTRO_FEATURES:remove = " nfs"
IMAGE_INSTALL:remove = " packagegroup-core-nfs-server"

KERNEL_DEVICETREE_BASENAME = "${@os.path.basename( \
		d.getVar('KERNEL_DEVICETREE').split()[0] \
		if d.getVar('KERNEL_DEVICETREE_EXT') == '' \
		else d.getVar('KERNEL_DEVICETREE_EXT'))}"
