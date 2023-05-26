PV = "5.15.96"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "4409aea474f6e8e8702c27d88033b64f3b1b5639"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
