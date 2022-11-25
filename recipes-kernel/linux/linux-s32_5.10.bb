PV = "5.10.145"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "0dce254e5cfe6052991e6e186b297f30d86e42d7"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
