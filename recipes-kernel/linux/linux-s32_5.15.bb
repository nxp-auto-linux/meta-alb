PV = "5.15.129"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "a503d505f3c1c0dec18f292be113d1b59d238716"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
