PV = "5.10.158"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "109e1ae85bf337dc4d9d3aa38ea0b89dafb6b4d1"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
