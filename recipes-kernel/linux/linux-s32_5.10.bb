PV = "5.10.184"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "a882e5dd8450033cd72d4243477fc210063db694"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
