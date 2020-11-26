# Copyright 2018,2019 NXP
#
# The minimal rootfs with basic packages for boot on VDK
#

require recipes-fsl/images/fsl-image-base.bb

# Support for Inter-Process(or) Communication over Shared Memory (ipc-shm) under Linux
IMAGE_INSTALL_append_s32g274sim = " ipc-shm "
IMAGE_INSTALL_append_s32r45sim = " ipc-shm "

IMAGE_INSTALL_append_gen1 = "${@bb.utils.contains('DISTRO_FEATURES', 'gmac', ' ${GMAC_IMAGE_INSTALL} ', '', d)}"
