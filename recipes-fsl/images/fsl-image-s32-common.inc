#
# Copyright 2017-2021 NXP
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

# Add OP-TEE user-space components
IMAGE_INSTALL_append_s32g = "${@bb.utils.contains('DISTRO_FEATURES', 'optee', ' optee-client optee-examples optee-test ', '', d)}"
