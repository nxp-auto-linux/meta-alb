PV = "5.10.194"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "bec61a6b8e3d5d0e489948d26745ad90606b7cec"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
