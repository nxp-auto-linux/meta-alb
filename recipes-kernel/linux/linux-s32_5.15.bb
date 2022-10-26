PV = "5.15.73"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "633350ed57ea23409ae5d2e5e3cbbab8919b5c4b"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
