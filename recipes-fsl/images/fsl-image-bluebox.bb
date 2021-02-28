require crosslayer-image-full.inc

EXTRA_REQUIRE ?= ""
require ${EXTRA_REQUIRE}

require fsl-image-blueboxadditions.inc
# Do not include virtualization user space libraries for s32v
# Note: since we are including lxc and meta-virtualization for s32v also,
# we may consider adding the user space libraries also for s32v in the future.
require ${@ "fsl-image-blueboxadditionsvirt.inc" if "s32v" not in (d.getVar("MACHINEOVERRIDES") or "").split(":") else "" }

# We want to have an itb to boot from in the /boot directory to be flexible
# about U-Boot behavior
IMAGE_INSTALL += "\
    linux-kernelitb-norootfs-image \
"

# Stuff that may not be in release SDKs, but we want it for the BlueBox
# to be able to control it better on BlueBox Classic chassis
IMAGE_INSTALL_append_ls2080ardb = " \
    lmsensors-fancontrol \
    lmsensors-pwmconfig \
"
IMAGE_INSTALL_append_ls2080abluebox = " \
    lmsensors-fancontrol \
    lmsensors-pwmconfig \
"
IMAGE_INSTALL_append_ls2084abluebox = " \
    lmsensors-fancontrol \
    lmsensors-pwmconfig \
"
