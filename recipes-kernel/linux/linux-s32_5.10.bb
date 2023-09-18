PV = "5.10.184"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "707f54307ebab36e43d08401f41e6fab364c6dfa"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
