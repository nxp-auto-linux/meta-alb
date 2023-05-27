PV = "5.10.176"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "b53f8a9bfb6ab75a6e6fbc51957d9d11c21c4fb8"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
