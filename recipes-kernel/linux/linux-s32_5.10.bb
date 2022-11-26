PV = "5.10.145"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "9abe0d3a1a1fa98573837f15750f2d02836c3492"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
