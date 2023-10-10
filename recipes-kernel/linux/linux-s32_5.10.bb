PV = "5.10.194"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "c20927a2e1c65b758a7c5da327fc264557a4c38a"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
