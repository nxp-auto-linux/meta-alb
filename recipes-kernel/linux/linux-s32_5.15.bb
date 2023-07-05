PV = "5.15.119"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "4b96f9d2536f43b5368f2655acbc4bb6dd521d28"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
