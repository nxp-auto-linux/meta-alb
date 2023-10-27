PV = "5.15.129"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "cd63f62d5081622e98bdf15202d0dabd98747c0d"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
