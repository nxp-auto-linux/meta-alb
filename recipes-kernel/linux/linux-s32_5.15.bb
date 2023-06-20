PV = "5.15.96"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "c64300b3019b561827c29de7f69356e0b9d10c6d"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
