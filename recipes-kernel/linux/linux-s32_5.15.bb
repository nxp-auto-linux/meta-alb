PV = "5.15.55"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "70cf2ebf061be473931ba122ae700837159aaa5a"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
