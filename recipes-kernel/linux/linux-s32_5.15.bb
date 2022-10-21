PV = "5.15.55"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "3fc5e9c469ad260de8ab1fe902583766f9a2dba8"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
