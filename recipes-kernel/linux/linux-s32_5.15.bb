PV = "5.15.119"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "f53dfa0c6a9ed916c26f1248868c7fc3f7ec96b3"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
