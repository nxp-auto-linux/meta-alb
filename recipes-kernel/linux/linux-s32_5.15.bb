PV = "5.15.85"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "60b3ed858b1a15a03560fc1de2601120390f0f34"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
