PV = "5.15.85"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "087c80a98cc4ebc1d967ff3e6fda0d3579501255"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
