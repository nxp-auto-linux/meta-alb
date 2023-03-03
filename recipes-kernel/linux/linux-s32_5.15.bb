PV = "5.15.85"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "b7a6f18ec367d5afb2e33ef61b0218dd8856864e"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
