PV = "5.10.176"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "f1e278d4bd4a0837064d974d7e2c2b4afb5121e6"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
