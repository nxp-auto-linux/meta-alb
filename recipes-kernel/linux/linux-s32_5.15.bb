PV = "5.15.73"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "7e91c88426614ce4deaf0c93d3daf268ecc466f8"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
