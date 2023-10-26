PV = "5.10.194"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "fbef8e8b669d832096376b503814dd00735a481f"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
