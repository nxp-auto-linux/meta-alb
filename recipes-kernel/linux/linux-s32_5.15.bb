PV = "5.15.73"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "e4a0346d6a9466b91bed37baea5e115ff8dff577"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
