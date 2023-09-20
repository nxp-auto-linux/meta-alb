PV = "5.10.184"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "a77e2493185d1b6da2c6ac115fce0893bf2e6ce0"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
