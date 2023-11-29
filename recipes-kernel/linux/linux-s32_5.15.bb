PV = "5.15.129"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "806dd1ba535c5004ca40cd252e335df5456870fd"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
