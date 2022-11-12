PV = "5.10.145"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "a2d0511e47bb72ec1574ec39df650318b4a0aa63"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
