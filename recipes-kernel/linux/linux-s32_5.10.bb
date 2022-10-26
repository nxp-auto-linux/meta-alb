PV = "5.10.145"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "608d2b41dfb96ed947971cfa04cb7cbfe9e4d036"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
