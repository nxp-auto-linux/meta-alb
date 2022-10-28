PV = "5.10.145"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "20f46f51aefd4b2a091ac04915d488ed850a6bf5"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
