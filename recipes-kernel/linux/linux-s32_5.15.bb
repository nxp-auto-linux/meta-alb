PV = "5.15.119"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "91cbe19be4b086973f434f53741c45ea1299c468"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
