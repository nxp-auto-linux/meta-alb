PV = "5.10.194"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "80745c85afda53806d58ea3036919a27100d3397"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
