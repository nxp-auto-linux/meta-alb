PV = "5.15.96"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "5dd3dbc91072133f083777c8dab5c351b1d0bbab"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
