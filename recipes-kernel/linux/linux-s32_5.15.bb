PV = "5.15.96"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "6fee7cc8e09266b8e5dc46eeb7f176dc522a283c"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
