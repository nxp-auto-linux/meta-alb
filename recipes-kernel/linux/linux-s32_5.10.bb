PV = "5.10.158"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "25879f99581630a04acd339dc623ed55b574bcef"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
