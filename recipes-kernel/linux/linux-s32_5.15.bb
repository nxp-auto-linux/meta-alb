PV = "5.15.119"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "83b7337b6b6c2df8a33959cb7264665becce1877"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
