PV = "5.10.176"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "bdea04c6c047b157c300576ea42de5a21cba1187"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
