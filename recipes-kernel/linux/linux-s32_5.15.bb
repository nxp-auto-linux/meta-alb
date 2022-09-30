PV = "5.15.55"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "54088ce067fa5fe96213e742a1f708c6fa651684"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
