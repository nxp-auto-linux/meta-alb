PV = "5.10.120"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "0b76731696c17e9bae9f61ba568fc73aeaa83244"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
