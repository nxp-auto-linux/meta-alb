PV = "5.15.129"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "09d31641e16e00ddfce11ec068f5dca5010a9d23"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
