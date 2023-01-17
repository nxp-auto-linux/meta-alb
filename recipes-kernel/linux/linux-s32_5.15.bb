PV = "5.15.85"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "1082ddc83a05ceb42781f65b0c66f36e6d91228d"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
