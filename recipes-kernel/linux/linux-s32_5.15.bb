PV = "5.15.129"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "2abc75ad602fa5993517c00c50915f1af50e5e95"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
