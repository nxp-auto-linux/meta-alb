require crosslayer-image-full.inc
require ${@ "fsl-image-s32-common.inc" if "s32v" in (d.getVar("MACHINEOVERRIDES") or "").split(":") else "" }
inherit distro_features_check

require fsl-image-blueboxadditions.inc
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
IMAGE_INSTALL_append_t4bluebox += " \
    lmsensors-fancontrol \
    lmsensors-pwmconfig \
"

# SDK 2.0 does not apply PCDs on T4 by default, which limits performance
# on the 10G ports unnecessarily
IMAGE_INSTALL_append_t4bluebox = "\
    pcd-init \
"

