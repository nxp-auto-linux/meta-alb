PV = "5.10.158"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "387b8c4a8106ca5347751422a82789084ddd7537"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
