PV = "5.10.194"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "656f49398bf1955462843eac5a234a1447d887b1"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
