PV = "5.15.129"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "dafbf6e60df766a41c97093277844f7c6b3a6b39"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
