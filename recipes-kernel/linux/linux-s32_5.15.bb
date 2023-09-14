PV = "5.15.119"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "746f1e948964ddd9fa2c7154e408177a39bd29a6"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
