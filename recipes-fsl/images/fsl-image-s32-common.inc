#
# Copyright 2017,2018 NXP
#

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

# Support for Inter-Process(or) Communication over Shared Memory (ipc-shm) under Linux
IMAGE_INSTALL_append_s32v234evb += " ipc-shm "
IMAGE_INSTALL_append_s32v234sbc += " ipc-shm "

IMAGE_INSTALL_append_gen1 ?= "${@bb.utils.contains('DISTRO_FEATURES', 'gmac', ' gmac ', '', d)}"
