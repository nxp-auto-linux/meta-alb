# Provides common settings for S32N boards, simulation and emulation models
require conf/machine/include/s32-base.inc
require conf/machine/include/arm/armv8-2a/tune-cortexa78ae.inc
require conf/machine/include/s32n-image.inc
include conf/machine/include/s32n-extra.inc

SOC_FAMILY:append = ":s32n"

DEFAULTTUNE:s32n ?= "cortexa78ae"

PREFERRED_VERSION_linux-s32:s32n = "6.12%"

# For the FCONF layer in TF-A
HOSTTOOLS += "gdb"

KERNEL_DEFCONFIG = "s32n7_defconfig"

IMAGE_CLASSES += " \
	image_types_fsl_ufs \
	image_types_fsl_sdcard \
"

EXTRA_IMAGECMD:ext4 = "-i 4096 -b 4096 -L ${IMAGE_NAME}"
IMAGE_FSTYPES:remove = "flashimage"
IMAGE_FSTYPES:append = " ufs sdcard"
IMAGE_FSTYPES:append = " cpio.gz.u-boot ext4"

UBOOT_SUFFIX_UFS = "${UBOOT_SUFFIX}"

UBOOT_CONFIG ?= "ufs sdcard"
UBOOT_CONFIG[ufs] = "${UBOOT_DEFCONFIG},,u-boot-nodtb.${UBOOT_SUFFIX}"
UBOOT_CONFIG[sdcard] = "${UBOOT_MMC_DEFCONFIG},,u-boot-nodtb.${UBOOT_SUFFIX}"

SERIAL_CONSOLES ?= "115200;ttyAMA0"
SERIAL_CONSOLES_CHECK ?= "ttyAMA0:ttyAMA0 ttyAMA1:ttyAMA1 ttyAMA2:ttyAMA2 " \
                         "ttyAMA3:ttyAMA3 ttyAMA4:ttyAMA4 ttyAMA5:ttyAMA5 " \
			 "ttyAMA6:ttyAMA6 ttyAMA7:ttyAMA7"

# Set to "1" when using an out-of-tree DTB for TF-A. The device tree source is expected to
# be found in the TF-A ${WORKDIR}.
TFA_DTB_EXT ?= "0"

# Enable FIP Authentication
require ${@bb.utils.contains('DISTRO_FEATURES', 'secboot', 'conf/machine/include/hse-secboot.inc', '', d)}
