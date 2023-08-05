PV = "5.15.119"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "b69691c51b7b83f53f383abd024b7d3dfc4a3c82"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
