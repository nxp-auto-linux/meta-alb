PV = "5.15.73"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "ff8d638b0f648b7619ad8fd4d9b22d88f0ed0b5b"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
