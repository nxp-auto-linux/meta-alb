PV = "5.15.96"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "0c36f24cd666b6a7f820cac6ae36b42944a84506"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
