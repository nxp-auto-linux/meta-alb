PV = "5.15.85"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "024254fa105034ce257c3a6de916e3a73547773f"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
