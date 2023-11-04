PV = "5.15.129"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "a628a2943e884620ad29c015b6500260e96b82e3"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
