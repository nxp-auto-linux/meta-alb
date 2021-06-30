#
# Copyright 2017-2021 NXP
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

# Add packages required for OpenMPI demo
# TODO: add them to the RDEPENDS list in the OpenMPI demo recipe
IMAGE_INSTALL_append_s32v2xx += "imagemagick gnuplot mpich mpich-dev"

# Support for Inter-Process(or) Communication over Shared Memory (ipc-shm) under Linux
IPCSHM ?= "ipc-shm"
IMAGE_INSTALL_append_s32v234evb += " ${IPCSHM} "
IMAGE_INSTALL_append_s32v234sbc += " ${IPCSHM} "
IMAGE_INSTALL_append_s32g2 += " ${IPCSHM} "
IMAGE_INSTALL_append_s32r45evb += " ${IPCSHM} "

IMAGE_INSTALL_append_gen1 = "${@bb.utils.contains('DISTRO_FEATURES', 'gmac', ' ${GMAC_IMAGE_INSTALL} ', '', d)}"

# Support CAN interfaces under Linux
IMAGE_INSTALL_append += "\
    can-utils \
    iproute2 \
"

# Add OP-TEE user-space components
IMAGE_INSTALL_append_s32g2 = "${@bb.utils.contains('DISTRO_FEATURES', 'optee', ' optee-client optee-examples optee-test ', '', d)}"
