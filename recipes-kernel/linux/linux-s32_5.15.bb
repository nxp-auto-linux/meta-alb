PV = "5.15.119"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "21dd083c3fd083280a3918f6c1d35f7078b6b435"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
