PV = "5.15.55"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "2819710e255159d7dc8b46e8565262341b9e5fa6"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
