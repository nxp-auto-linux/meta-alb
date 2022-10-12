PV = "5.10.145"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "7e2556f7e2c7a1608969da1fbefcdd05c0abfce8"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
