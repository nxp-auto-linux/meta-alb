require recipes-kernel/linux/linux-s32.inc

SRCREV = "71110d844aafa5a64e89425cfa651455c98e5e2e"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

# Enable 100MB BAR support for S32G and R
SRC_URI_append_gen1 += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-large-bars', \
	'file://0001-s32gen1-Increase-reserved-mem-and-EP-BAR-2-to-100MB-${PV}.patch', '', d)}"
