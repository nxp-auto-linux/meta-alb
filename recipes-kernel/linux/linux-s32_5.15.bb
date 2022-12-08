PV = "5.15.73"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "56f2c94545c05746bc82c3d9b8a1c77b8cd5994c"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
