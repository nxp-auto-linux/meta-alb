PV = "5.10.194"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "649ce2c39a0308c464522407a2c2ad2f2f7effdc"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
