PV = "5.15.119"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "2362b5bcb282f489298e0fed330127a6d0a4fde7"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
