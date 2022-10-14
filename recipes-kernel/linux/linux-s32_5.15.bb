PV = "5.15.55"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "c95222fe51d60f36706786af1965c138f1448f83"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
