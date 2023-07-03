PV = "5.10.176"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "b5a771917a2f46c6156522f6271f5492747df0af"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
