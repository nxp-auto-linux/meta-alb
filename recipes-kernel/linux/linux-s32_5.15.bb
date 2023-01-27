PV = "5.15.85"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "660368f1691d215e6ef05d6df1be66b99c3b6d4d"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
