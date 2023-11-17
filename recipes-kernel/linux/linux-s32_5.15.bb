PV = "5.15.129"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "6858a55d2a18acee000392ee0ae8dbb29a1535bf"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
