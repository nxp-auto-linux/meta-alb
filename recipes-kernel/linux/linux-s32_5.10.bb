PV = "5.10.194"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "164686016daf854b9cd7629a8ef41a7cf5a604e3"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
