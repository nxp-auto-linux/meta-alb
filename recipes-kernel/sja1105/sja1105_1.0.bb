# Copyright 2017,2018 NXP

SUMMARY = "Add support for SJA1105 switch for S32V234EVB and BB Mini"
LICENSE = "GPLv2+ & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cd21f59260ac175d0ec640af2d326f44"

inherit module

SRC_URI = "git://bitbucket.sw.nxp.com/scm/alb/sja1105x.git;branch=develop;protocol=https"
SRCREV = "120019a4df79576e9713846dbf7d0a49c27d9067"

S = "${WORKDIR}/git"
DESTDIR = "${D}{prefix}"
EXTRA_OEMAKE_append_s32v234evb = " EVB=1"
EXTRA_OEMAKE_append_s32v234bbmini = " BBMINI=1"

MODULES_MODULE_SYMVERS_LOCATION = "drivers/modules"

do_install_append() {
	oe_runmake tools_install INSTALL_DIR=${D}
	install -d ${D}/${sysconfdir}/init.d
	install -d ${D}/${sysconfdir}/rc5.d
	install -d ${D}/${sysconfdir}/rc3.d
	install -m 755 ${S}/drivers/modules/sja1105.sh ${D}/${sysconfdir}/init.d/sja1105.sh
	echo "insmod /lib/modules/\`uname -r\`/kernel/drivers/net/phy/nxp/nxp.ko" >> ${D}/${sysconfdir}/init.d/sja1105.sh
	ln -sf ../init.d/sja1105.sh      ${D}${sysconfdir}/rc5.d/S90sja1105.sh
	ln -sf ../init.d/sja1105.sh      ${D}${sysconfdir}/rc3.d/S90sja1105.sh
}

FILES_${PN} += "${base_libdir}/*"
FILES_${PN} += "${sysconfdir}/init.d/*"
FILES_${PN} += "${sysconfdir}/rc5.d/*"
FILES_${PN} += "${sysconfdir}/rc3.d/*"

PROVIDES = "kernel-module-sja1105${KERNEL_MODULE_PACKAGE_SUFFIX}"
RPROVIDES_${PN} = "kernel-module-sja1105${KERNEL_MODULE_PACKAGE_SUFFIX}"

COMPATIBLE_MACHINE = "s32v234evb|s32v234bbmini"
INHIBIT_PACKAGE_STRIP = "1"
