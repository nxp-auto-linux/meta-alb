PV = "5.10.176"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "75c56b68362e7c236d22bf9ef8346a550c5536f6"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
