PV = "5.10.145"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "1594b25154a2db746972517c61cbd0935366391a"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
