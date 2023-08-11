PV = "5.10.184"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "ae257a7b26c2869caf8dff54a823ab7cc8dfc80c"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
