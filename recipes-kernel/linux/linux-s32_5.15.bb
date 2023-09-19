PV = "5.15.119"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "99225ee7335bb2065abe0b1e1da07e4e0d4af29d"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
