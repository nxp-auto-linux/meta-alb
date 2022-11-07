#
# Copyright 2017-2022 NXP
#

# Support for Inter-Process(or) Communication over Shared Memory (ipc-shm) under Linux
IPCSHM ?= "ipc-shm"
IMAGE_INSTALL:append:s32g = " ${IPCSHM} "
IMAGE_INSTALL:append:s32r45evb = " ${IPCSHM} "

# Support CAN interfaces under Linux
IMAGE_INSTALL:append = " \
    can-utils \
    iproute2 \
"
