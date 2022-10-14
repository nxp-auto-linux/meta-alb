PV = "5.15.55"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "f4789e8a37c0738959695d4e01f1946bf1c7c90e"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
