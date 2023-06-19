PV = "5.15.96"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "3552dbb0407905fcf869aa36a063608fad9cab1d"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
