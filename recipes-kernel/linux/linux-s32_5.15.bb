PV = "5.15.129"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "cdac0506874b7e6a277f12e72e3900d2a410d909"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
