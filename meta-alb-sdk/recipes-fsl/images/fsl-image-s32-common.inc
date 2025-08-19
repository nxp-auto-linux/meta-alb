#
# Copyright 2017-2024 NXP
#

IPCSHM ?= "ipc-shm"

IMAGE_INSTALL:append:s32g = " ${IPCSHM} "
IMAGE_INSTALL:append:s32r45 = " ${IPCSHM} "
IMAGE_INSTALL:append:s32r478 = " ${IPCSHM} "

# Support CAN interfaces under Linux
IMAGE_INSTALL:append = " \
    can-utils \
    can-utils-cantest \
    iproute2 \
"
