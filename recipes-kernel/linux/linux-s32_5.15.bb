PV = "5.15.119"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "3b13c01463c2093479827c2e8a1f7443bc42788c"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
