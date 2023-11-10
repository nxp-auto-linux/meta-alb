PV = "5.15.129"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "cbf32fdcb1c3d4c8ca1ddd3580652a778fefc769"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
