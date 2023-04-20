PV = "5.15.96"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "0648e9662a112b5fb134fae30953e8d92c030b1f"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
