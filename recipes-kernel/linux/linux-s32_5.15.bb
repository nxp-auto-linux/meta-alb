PV = "5.15.129"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "4f503e7ef58e92e8ee26b7c8f6947bc95dbac3f7"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
