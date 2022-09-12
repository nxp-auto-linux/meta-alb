PV = "5.10.120"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "7e1a8922b0e4d4916b02f0442e1c9b85c15a46c9"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
