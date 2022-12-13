# Copyright 2022 NXP

DESCRIPTION = "ARM Trusted Firmware Tools"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://license.rst;md5=1dd070c98a281d18d9eefd938729b031"

require recipes-bsp/arm-trusted-firmware/atf-src.inc

BBCLASSEXTEND = "native"
DEPENDS += "openssl-native"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

do_compile() {
	oe_runmake -C "${S}" fiptool
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/tools/fiptool/fiptool ${D}/${bindir}/
}

FILES_${PN} = "${bindir}/fiptool"
