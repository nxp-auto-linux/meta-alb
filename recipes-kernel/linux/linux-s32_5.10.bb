PV = "5.10.194"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "3ec0f4498322168914de6fb4cd8e771c77cfa5c0"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
