PV = "5.15.129"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "b67e75a35fe7b2c56166b21c04a012c9bb9ac59e"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
