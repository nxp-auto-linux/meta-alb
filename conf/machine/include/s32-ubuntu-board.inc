require conf/machine/include/ubuntubasics.inc

FLASHIMAGE_ROOTFS = "fsl-image-ubuntu-base"

BOOT_SCRIPTS = ""
DISTRO_FEATURES:append = " docker"
