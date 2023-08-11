PV = "5.15.119"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "106bc19e734e2f26cc2ed26397b7f4c30e1c8dcf"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
