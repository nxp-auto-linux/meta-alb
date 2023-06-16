PV = "5.10.176"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "0a457d9971c128287fb6b4ed1955955e9e6126a7"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
