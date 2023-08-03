PV = "5.15.119"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "9fbd213e88a8e14d6e6ec353b1d46fd9e5885a75"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
