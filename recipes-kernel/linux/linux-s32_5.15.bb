PV = "5.15.73"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "04f875d1d3e85a30b674de25a3fa4547b5820c05"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
