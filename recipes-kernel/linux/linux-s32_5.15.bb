PV = "5.15.55"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "8fb717f59603bc2b2a76beca0f2ca7a8c01b59e9"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
