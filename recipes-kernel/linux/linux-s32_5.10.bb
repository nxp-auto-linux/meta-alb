PV = "5.10.176"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "6725a96348c10f44371ff4fdfd869984184ecb94"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
