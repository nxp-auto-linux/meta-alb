PV = "5.15.119"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "1d8ee9cba6c4e675e6c71e4572d83ae15014be4a"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
