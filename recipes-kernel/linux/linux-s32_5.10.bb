PV = "5.10.194"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "3c5daf22e1b45312b82d91317329da4d4a4c886f"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
