PV = "5.15.96"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "002ec61dd372d6f48d712744f6a4e0c3f986a670"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
