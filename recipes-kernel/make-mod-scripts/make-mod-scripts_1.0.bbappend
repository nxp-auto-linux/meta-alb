# This is needed in order to compile Linux Kernel
# version 5.15 on Yocto Gatesgarth version.
# This patch already exists in Yocto Hardknott version.
EXTRA_OEMAKE += "CROSS_COMPILE=${TARGET_PREFIX}"
