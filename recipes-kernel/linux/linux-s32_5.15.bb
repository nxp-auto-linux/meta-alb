PV = "5.15.85"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "3099505a5efe869acc6f52a669efef3be5038ec1"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
