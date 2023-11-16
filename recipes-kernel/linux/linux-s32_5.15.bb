PV = "5.15.129"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "6f46bf00c770b0a4e477267a39fd8c3ac8ca0030"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
