#
# Copyright 2017-2024, 2026 NXP
#

IPCSHM ?= "ipc-shm"

IMAGE_INSTALL:append:s32g = "${@bb.utils.contains('PREFERRED_PROVIDER_virtual/kernel', 'linux-s32', ' ${IPCSHM} ', '', d)}"
IMAGE_INSTALL:append:s32r45 = " ${IPCSHM} "
IMAGE_INSTALL:append:s32r478 = " ${IPCSHM} "

# Support CAN interfaces under Linux
IMAGE_INSTALL:append = " \
    can-utils \
    can-utils-cantest \
    iproute2 \
"
