PV = "5.10.194"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "ee7d794e334615b163e33ae078ba6461730c6c9e"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
