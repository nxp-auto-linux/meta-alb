PV = "5.10.194"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "f28852b8df685fa9e6841215268f330551cac04a"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
