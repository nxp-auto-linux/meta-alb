PV = "5.15.129"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "76e12f6335862c5ebc9fad413561a8b611a87ac0"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
