PV = "5.10.176"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "d2b53d35895f4ec98aabd484ad517641159be5ee"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
