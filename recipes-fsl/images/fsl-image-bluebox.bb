require crosslayer-image-full.inc

EXTRA_REQUIRE ?= ""
require ${EXTRA_REQUIRE}

require fsl-image-blueboxadditions.inc
require fsl-image-blueboxadditionsvirt.inc

# We want to have an itb to boot from in the /boot directory to be flexible
# about U-Boot behavior
IMAGE_INSTALL += "\
    linux-kernelitb-norootfs-image \
"

# Stuff that may not be in release SDKs, but we want it for the BlueBox
# to be able to control it better on BlueBox Classic chassis
IMAGE_INSTALL:append:ls2084abluebox = " \
    lmsensors-fancontrol \
    lmsensors-pwmconfig \
"
