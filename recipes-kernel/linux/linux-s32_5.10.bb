PV = "5.10.176"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "62de76fbcd66e8416a8f85171fcc8c88821165ae"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
