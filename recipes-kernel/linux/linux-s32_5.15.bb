PV = "5.15.73"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "afb904c8056b38a16770a22cb83b597751236e7b"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
