# Copyright 2017 NXP

# The LS2 EAR5 SDK adds Layerscape specific elements
# as hard requirement. We want to benefit from the
# LS style rootfs image sets, but we can't use the
# extra configuration that is not buildable for
# the S32V2xx parts.
# Obviously, the Layerscape image sets also contain
# tools and elements not applicable, but here
# simple reuse is convenient and effective for all
# the common elements.
# TODO: Create a proper image description that
# includes the right things from the start

DEPLOY_PKGS = ""

IMAGE_INSTALL_remove += "u-boot-ls2"
IMAGE_INSTALL_remove += "packagegroup-fsl-dpaa2"

IMAGE_INSTALL_remove += "restool"
IMAGE_INSTALL_remove += "echo-server"

IMAGE_INSTALL += "nfs-utils fbset-modes lrzsz glibc-utils"

# Support for SJA1105 swich under Linux
IMAGE_INSTALL_append_s32v234evb = " sja1105 "

# Enable LXC features
IMAGE_INSTALL_append = "${@bb.utils.contains('DISTRO_FEATURES', 'lxc', ' lxc', '', d)}"

IMAGE_FSTYPES_s32v2xx = "tar.gz ext3 sdcard cpio.gz"
