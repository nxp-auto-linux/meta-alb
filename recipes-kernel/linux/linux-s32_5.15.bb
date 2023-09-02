PV = "5.15.119"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "23cf78e8cc52d6ea600dd0b81abf8a9cab23712e"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
