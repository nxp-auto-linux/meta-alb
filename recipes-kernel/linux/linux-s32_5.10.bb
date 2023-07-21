PV = "5.10.184"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "0f9b05e74c3dff9e671f5beaca732e2cb7c51541"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
