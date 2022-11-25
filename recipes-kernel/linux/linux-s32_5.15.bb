PV = "5.15.73"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "aeccc80c606a87756ce77ba0526d0c87bd8acda2"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
