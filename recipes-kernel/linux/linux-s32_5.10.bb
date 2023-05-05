PV = "5.10.176"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "f5207ca8a86727938c242592809f45ef4f0e0a66"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
