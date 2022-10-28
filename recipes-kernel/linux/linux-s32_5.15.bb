PV = "5.15.73"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "068d3e8a91e35c0ff49aedda15274ea4b1839266"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
