PV = "5.15.85"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "6c67ffd8f165f704777d6718657a4f6f3d9827cd"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
