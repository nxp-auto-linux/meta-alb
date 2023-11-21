PV = "5.15.129"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "96fa36cd5b1d4b1495497214f3e6f4e7a2ecf149"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
