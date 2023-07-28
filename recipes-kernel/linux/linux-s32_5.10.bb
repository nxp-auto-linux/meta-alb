PV = "5.10.184"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "0d3eb7937761f0a623be435105cb46b50499ff6f"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
