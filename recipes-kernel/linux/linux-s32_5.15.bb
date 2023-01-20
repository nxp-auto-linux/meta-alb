PV = "5.15.85"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "49d029cc8672c4c6ba02b7e88499fa68a0b31a3e"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
