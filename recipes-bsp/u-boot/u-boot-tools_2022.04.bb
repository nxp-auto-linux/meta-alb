require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot-tools.inc
require recipes-bsp/u-boot/u-boot-src-${PV}.inc

DEPENDS += " \
	gnutls-native \
"

LIC_FILES_CHKSUM = "file://Licenses/README;md5=5a7450c57ffe5ae63fd732446b988025"
