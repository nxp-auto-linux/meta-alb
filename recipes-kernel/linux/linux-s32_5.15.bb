PV = "5.15.73"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "dd8e1a8c0632f4cf3b20a7be0bc139e4a59ef337"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
