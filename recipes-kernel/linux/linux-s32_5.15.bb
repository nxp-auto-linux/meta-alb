PV = "5.15.96"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "ac7f28f9bacd8d9d7d32b710f85bdf4b3bbd740b"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
