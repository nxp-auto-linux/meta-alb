PV = "5.15.73"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "e781ee2b86bdfa84ee6a2f9b7d7f4aa3db7a0f59"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
