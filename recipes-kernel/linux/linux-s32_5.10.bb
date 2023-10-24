PV = "5.10.194"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "d9375c4b8c9fbc0fe071d8f665952ac04ff7f420"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
