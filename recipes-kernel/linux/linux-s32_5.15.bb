PV = "5.15.73"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "2a3295b2f17fa4f43c934d7695583ef839e9bfa6"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
