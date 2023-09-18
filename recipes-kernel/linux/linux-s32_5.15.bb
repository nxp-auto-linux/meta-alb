PV = "5.15.119"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "e18f05316cd90bacc5d1b3d5f7caf44e12e2cbf5"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
