require conf/machine/include/s32-base.inc

SOC_FAMILY_append = ":s32v:s32v2xx"

UBOOT_SUFFIX ?= "s32"

IMAGE_FSTYPES += "ext4"

# Specific definitions for .sdcard image generation
IMAGE_FSTYPES += "sdcard"
SDCARD_ROOTFS = "ext4"
UBOOT_SUFFIX_SDCARD = "${UBOOT_SUFFIX}"
IMAGE_CLASSES += "image_types_fsl_sdcard"

# Permit later to optionally add flashimage to IMAGE_FSTYPES
IMAGE_CLASSES += "image_types_fsl_flashimage"

UBOOT_CONFIG ?= "sdcard"
UBOOT_CONFIG[sdcard] ?= "${MACHINE}_config,,u-boot.${UBOOT_SUFFIX}"

# Setting the following variable disables the AVB TX queues (1 and 2) on S32V234 
# to prevent a FEC TX queue timeout that occurs when using NFS root filesystem.
# This issue occurs on S32V234 and we enable it by default for this platform
# for safety.
# The limitation can be disabled by setting it to 0 in local.conf.
KERNEL_FEC_LIMIT_TX ?= "1"

# On S32V, we support either pure SW rendering or HW assisted
# rendering. Libraries come from different sources then.
PREFERRED_PROVIDER_virtual/egl = "${@bb.utils.contains("DISTRO_FEATURES", "gpu", "gpu-viv-bin-s32v2xx", "mesa", d)}"
PREFERRED_PROVIDER_virtual/libgles1 = "${@bb.utils.contains("DISTRO_FEATURES", "gpu", "gpu-viv-bin-s32v2xx", "mesa", d)}"
PREFERRED_PROVIDER_virtual/libgles2 = "${@bb.utils.contains("DISTRO_FEATURES", "gpu", "gpu-viv-bin-s32v2xx", "mesa", d)}"

KERNEL_NAME = "${PREFERRED_PROVIDER_virtual/kernel}"
KERNEL_VER = '${@d.getVar("PREFERRED_VERSION_${KERNEL_NAME}",True)}'
PREFERRED_VERSION_kernel-module-galcore = "${@oe.utils.conditional('KERNEL_VER', '5.4', '6.4.0', '6.2.4', d)}"
