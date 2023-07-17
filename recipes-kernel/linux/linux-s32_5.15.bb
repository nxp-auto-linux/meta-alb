PV = "5.15.119"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "376845bae89cdee64ec430d1047495730c6616db"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
