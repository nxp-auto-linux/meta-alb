FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
	file://0001-u-boot-Basic-set-of-BB-Mini-configuration-added.patch \
	file://0001-u-boot-Adapted-the-ls2084abbmini-config-to-the-LSDK-.patch \
	file://0001-u-boot-qoriq-Missing-USB-nodes-in-the-dts-for-the-ls.patch \
	file://0002-u-boot-qoriq-Special-config-for-LS2080ARDB-with-LS20.patch \
"
