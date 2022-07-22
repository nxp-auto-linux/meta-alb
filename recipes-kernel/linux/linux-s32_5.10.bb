PV = "5.10.120"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "ab0c51dbf64bf0cf1e3690271afe27016a19ff55"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

# Enable 100MB BAR support for S32G and R (this feature tagets PCIE0 and BAR2 only)
SRC_URI_append_gen1 += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-large-bars', \
	'file://0001-s32cc-Increase-reserved-mem-and-EP-BAR-2-to-100MB-${PV_MAJ_VER}.patch', '', d)}"
