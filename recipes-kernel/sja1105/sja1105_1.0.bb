# Copyright 2017 NXP

SUMMARY = "Add support for SJA1105 switch for S32V234EVB and BB Mini"
LICENSE = "GPLv2+ & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cd21f59260ac175d0ec640af2d326f44"

inherit module

SRC_URI = "git://bitbucket.sw.nxp.com/scm/alb/sja1105x.git;branch=develop;protocol=https"
SRCREV = "2d2af15bf66837b13788161d01d2d4d1499e6ee5"

S = "${WORKDIR}/git"
DESTDIR = "${D}{prefix}"
EXTRA_OEMAKE_append_s32v234evb = " EVB=1"
EXTRA_OEMAKE_append_s32v234bbmini = " BBMINI=1"

do_install_append() {
	oe_runmake tools_install INSTALL_DIR=${D}
	install -d ${D}/${sysconfdir}/init.d
	install -d ${D}/${sysconfdir}/rc5.d
	install -m 755 ${S}/drivers/modules/sja1105.sh ${D}/${sysconfdir}/init.d/sja1105.sh
	ln -sf ../init.d/sja1105.sh      ${D}${sysconfdir}/rc5.d/S90sja1105.sh
}

FILES_${PN} += "${base_libdir}/*"
FILES_${PN} += "${sysconfdir}/init.d/*"
FILES_${PN} += "${sysconfdir}/rc5.d/*"

PROVIDES = "kernel-module-sja1105"
RPROVIDES_${PN} = "kernel-module-sja1105"

COMPATIBLE_MACHINE = "s32v234evb|s32v234bbmini"
INHIBIT_PACKAGE_STRIP = "1"
