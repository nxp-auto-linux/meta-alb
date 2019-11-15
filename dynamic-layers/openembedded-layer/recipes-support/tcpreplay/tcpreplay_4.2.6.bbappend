FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
	file://0001-configure.ac-fix-without-libdnet.patch \
"

EXTRA_OECONF += " --without-libdnet"
