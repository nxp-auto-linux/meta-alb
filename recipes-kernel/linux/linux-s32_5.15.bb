PV = "5.15.85"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "230261b4e12dfaf6ea9fb55b29fa40d668521e73"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
