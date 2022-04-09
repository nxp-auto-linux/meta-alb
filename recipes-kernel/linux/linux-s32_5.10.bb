PV = "5.10.109"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "7e1da3c85199ff20eec4fa808f9a95fc7c024903"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

# Enable 100MB BAR support for S32G and R
SRC_URI_append_gen1 += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-large-bars', \
	'file://0001-s32gen1-Increase-reserved-mem-and-EP-BAR-2-to-100MB-${PV_MAJ_VER}.patch', '', d)}"
