#
# Copyright 2017-2019 NXP
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

# Support for Inter-Process(or) Communication over Shared Memory (ipc-shm) under Linux
IMAGE_INSTALL_append_s32v234evb += " ipc-shm "
IMAGE_INSTALL_append_s32v234sbc += " ipc-shm "
IMAGE_INSTALL_append_s32g274aevb += " ipc-shm "

IMAGE_INSTALL_append_gen1 = "${@bb.utils.contains('DISTRO_FEATURES', 'gmac', ' ${GMAC_IMAGE_INSTALL} ', '', d)}"
IMAGE_INSTALL_append_s32g274aevb = "${@bb.utils.contains('DISTRO_FEATURES', 'pfe', ' pfe', '', d)}"

# Support CAN interfaces under Linux
IMAGE_INSTALL_append += "\
    can-utils \
    iproute2 \
"
