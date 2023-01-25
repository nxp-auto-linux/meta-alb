PV = "5.10.158"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "342c538037c6677efc6537924044ac25f4332b67"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
