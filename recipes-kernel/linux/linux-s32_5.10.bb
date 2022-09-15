PV = "5.10.120"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "0dd2393cff595396f63a85c14198a78d5103ddad"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
