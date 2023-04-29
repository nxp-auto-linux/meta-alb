PV = "5.15.96"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "79defc9fb50d112623a0653f12b89b1f7e8b154a"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
