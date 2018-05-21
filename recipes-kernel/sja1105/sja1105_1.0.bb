# Copyright 2017,2018 NXP

SUMMARY = "Add support for SJA1105 switch for S32V234EVB and BB Mini"
LICENSE = "GPLv2+ & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit module

SRC_URI = "git://source.codeaurora.org/external/autobsps32/sja1105x;branch=alb/master;protocol=https"
SRCREV = "42cf3ba62848725f0fb80a65b95062e2d279dd46"

S = "${WORKDIR}/git"
DESTDIR = "${D}"
EXTRA_OEMAKE_append = " INSTALL_DIR=${DESTDIR} KERNELDIR=${KBUILD_OUTPUT} MYCOMPILER=${CROSS_COMPILE}gcc "
EXTRA_OEMAKE_append_s32v234evb = " MYPLATFORM=evb "
EXTRA_OEMAKE_append_s32v234bbmini = " MYPLATFORM=bbmini "

do_install_append() {
	install -d ${D}/${sysconfdir}/modules-load.d
	echo "sja1105pqrs" >> ${D}/${sysconfdir}/modules-load.d/sja1105pqrs.conf
	echo "nxp" >> ${D}/${sysconfdir}/modules-load.d/nxp.conf
}

FILES_${PN} += "${base_libdir}/*"
FILES_${PN} += "${sysconfdir}/modules-load.d/*"

PROVIDES = "kernel-module-sja1105pqrs${KERNEL_MODULE_PACKAGE_SUFFIX}"
RPROVIDES_${PN} = "kernel-module-sja1105pqrs${KERNEL_MODULE_PACKAGE_SUFFIX}"

COMPATIBLE_MACHINE = "${@bb.utils.contains('PREFERRED_VERSION_linux-s32', '4.14', '', 's32v234evb|s32v234bbmini', d)}"
INHIBIT_PACKAGE_STRIP = "1"

DEPENDS_append = " coreutils-native"
