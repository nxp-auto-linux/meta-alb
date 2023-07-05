PV = "5.10.184"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "14f65d43a896a2fb45b1654c8c0e90b9848ad8ec"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
