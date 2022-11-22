PV = "5.10.145"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "237baf2b49e8a895d2c29ceda39042b839b514d2"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
