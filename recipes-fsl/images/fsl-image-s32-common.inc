#
# Copyright 2017-2023 NXP
#

# Support for Inter-Process(or) Communication over Shared Memory (ipc-shm) under Linux
IPCSHM ?= "ipc-shm"
IMAGE_INSTALL:append:s32g = " ${IPCSHM} "
IMAGE_INSTALL:append:s32r45 = " ${IPCSHM} "

# Support CAN interfaces under Linux
IMAGE_INSTALL:append = " \
    can-utils \
    can-utils-cantest \
    iproute2 \
"
