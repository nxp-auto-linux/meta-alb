require recipes-kernel/linux/linux-s32.inc

SRCREV = "a45eaead6e552542e87c98049656a6ae37bc77bd"

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

# Perf patches
SRC_URI += " \
    file://perf/0001-perf-cs-etm-Move-definition-of-traceid_list-global-v.patch \
"

# Enable 100MB BAR support for S32G and R
SRC_URI_append_gen1 += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-large-bars', \
	'file://0001-s32gen1-Increase-reserved-mem-and-EP-BAR-2-to-100MB-${PV}.patch', '', d)}"
