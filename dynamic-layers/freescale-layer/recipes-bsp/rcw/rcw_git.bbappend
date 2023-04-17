FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append = " \
	file://0001-rcw-Split-up-a050426.rcw-to-permit-customized-small-.patch \
"
