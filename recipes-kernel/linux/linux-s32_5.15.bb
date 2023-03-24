PV = "5.15.85"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "9133b22fcbe14598c74fbd233482450e01834483"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
