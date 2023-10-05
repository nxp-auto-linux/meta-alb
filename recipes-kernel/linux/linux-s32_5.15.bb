PV = "5.15.129"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "6391cb1d99c035fc88960d65ab927e0a3155b7dd"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
