PV = "5.10.120"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "6eddf0c00190f9b0f4de22a343c0959df9124343"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
