PV = "5.15.85"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "29f74a28e89974aca6e940ba8c932090d9551a52"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
