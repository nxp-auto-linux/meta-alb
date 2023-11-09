PV = "5.10.194"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "96f876c6daf4d5be9250e411f32bba81025d0679"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
