PV = "5.15.129"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "f6d24f2d9eb3bb96b3524581661cdd455404ff5e"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
