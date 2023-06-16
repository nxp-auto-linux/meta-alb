PV = "5.15.96"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "f2b25660adcf5713afcd24abdae0dbe69b199701"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
