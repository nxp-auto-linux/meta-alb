PV = "5.15.119"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "bebcd621f1ceec8d298bb1f9f562cb370d6596cd"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
