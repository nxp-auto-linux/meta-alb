#
# Copyright 2017-2022 NXP
#

# Support for Inter-Process(or) Communication over Shared Memory (ipc-shm) under Linux
IPCSHM ?= "ipc-shm"
IMAGE_INSTALL_append_s32g += " ${IPCSHM} "
IMAGE_INSTALL_append_s32r45evb += " ${IPCSHM} "

# Support CAN interfaces under Linux
IMAGE_INSTALL_append += "\
    can-utils \
    iproute2 \
"
