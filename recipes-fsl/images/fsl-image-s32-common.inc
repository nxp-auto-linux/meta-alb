#
# Copyright 2017,2018 NXP
#

# Enable LXC features
IMAGE_INSTALL_append_s32 = "${@bb.utils.contains('DISTRO_FEATURES', 'lxc', ' lxc', '', d)}"

# Support for native graphics. We enable this via DISTRO_FEATURES
# because we have to sync this choice with the config for mesa to
# avoid conflicts in egl.
IMAGE_INSTALL_append_s32v2xx += "\
    ${@bb.utils.contains("DISTRO_FEATURES", "gpu", "gpu-viv-bin-s32v2xx", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "gpu", "gpu-viv-bin-s32v2xx-samples", "", d)} \
"

IMAGE_INSTALL_append_s32v2xx += "\
    fbset-modes \
    fbset \
    fbv \
    u-boot-s32 \
"

# Support for the S32V CAN interfaces under Linux
IMAGE_INSTALL_append_s32v2xx += "\
    canutils \
"