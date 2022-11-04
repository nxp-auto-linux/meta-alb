PV = "5.10.145"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "0c6c41a45c1ad3c5876fa422099313dea786b3fb"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
