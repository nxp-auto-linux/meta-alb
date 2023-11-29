PV = "5.10.194"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "afe96d2ae0a7f838024c4a8eabff17faab1ed9a6"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
