PV = "5.15.55"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "ca01b1f5b81d581951dadbe21228099d466e399b"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
