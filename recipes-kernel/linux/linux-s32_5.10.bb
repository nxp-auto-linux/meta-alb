PV = "5.10.145"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "6fa243691db354ee80e99d55e3133e59d5f85a9e"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
