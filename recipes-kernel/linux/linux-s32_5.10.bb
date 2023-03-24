PV = "5.10.158"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "d907314dc2362ccc4d9c453a21df6159a4558c70"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
