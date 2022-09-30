PV = "5.10.120"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "c10a07611e51e69f2b239b9d1e1ad348d94de376"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
