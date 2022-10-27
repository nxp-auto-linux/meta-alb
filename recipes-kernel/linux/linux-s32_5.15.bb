PV = "5.15.73"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "74c3616bd502ef2e2f763ff3b3d21cff8688c286"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
