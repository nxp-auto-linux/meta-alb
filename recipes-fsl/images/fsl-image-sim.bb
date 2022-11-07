# Copyright 2018,2019 NXP
#
# The minimal rootfs with basic packages for boot on VDK
#

require recipes-fsl/images/fsl-image-base.bb

# Support for Inter-Process(or) Communication over Shared Memory (ipc-shm) under Linux
IMAGE_INSTALL:append:s32g274sim = " ipc-shm "
IMAGE_INSTALL:append:s32r45sim = " ipc-shm "
