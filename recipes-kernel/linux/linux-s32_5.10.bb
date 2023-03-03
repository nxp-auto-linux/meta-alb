PV = "5.10.158"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "c81c4a1de1293ae352b7ac118e841638c54dbca8"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
