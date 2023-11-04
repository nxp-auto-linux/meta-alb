PV = "5.10.194"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "56882f070104ca6af1ec6962f997f03b7f7bff26"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
