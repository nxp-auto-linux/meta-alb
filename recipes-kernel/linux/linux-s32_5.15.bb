PV = "5.15.96"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "2e4c5f2f3a0443614d6b6567848735479115c138"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
