# Copyright 2017,2018 NXP

SUMMARY = "Add support for SJA1105 switch for S32V234EVB and BB Mini"
LICENSE = "GPLv2+ & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit module

SRC_URI_4.1 = "git://source.codeaurora.org/external/autobsps32/sja1105x;branch=alb/master;protocol=https"
SRCREV_4.1 = "f0c783abe9e6aad16f2188bff941e6a0e237cd56"

SRC_URI_4.14 = "git://bitbucket.sw.nxp.com/scm/alb/sja1105x.git;branch=develop-4.14;protocol=https"
SRCREV_4.14 = "7ecff1e84b2251e37a019c0345d600e9a43c1a40"

KERNEL_NAME = "${PREFERRED_PROVIDER_virtual/kernel}"

SRC_URI = '${@base_conditional("PREFERRED_VERSION_${KERNEL_NAME}", "4.14", "${SRC_URI_4.14}", "${SRC_URI_4.1}", d)}'
SRCREV = '${@base_conditional("PREFERRED_VERSION_${KERNEL_NAME}", "4.14", "${SRCREV_4.14}", "${SRCREV_4.1}", d)}'

S = "${WORKDIR}/git"
DESTDIR = "${D}"
EXTRA_OEMAKE_append = " INSTALL_DIR=${DESTDIR} KERNELDIR=${KBUILD_OUTPUT} MYCOMPILER=${CROSS_COMPILE}gcc "
EXTRA_OEMAKE_append_s32v234evb = " MYPLATFORM=evb "
EXTRA_OEMAKE_append_s32v234bbmini = " MYPLATFORM=bbmini "

do_install_append() {
	install -d ${D}/${sysconfdir}/init.d
	install -d ${D}/${sysconfdir}/rc5.d
	install -d ${D}/${sysconfdir}/rc3.d
	install -m 755 ${WORKDIR}/sja1105.sh ${D}/${sysconfdir}/init.d/sja1105.sh
	echo "lsmod | grep -q \"^nxp \"  > /dev/null || insmod /lib/modules/\`uname -r\`/kernel/drivers/net/phy/nxp/nxp.ko" >> ${D}/${sysconfdir}/init.d/sja1105.sh
	ln -sf ../init.d/sja1105.sh      ${D}${sysconfdir}/rc5.d/S90sja1105.sh
	ln -sf ../init.d/sja1105.sh      ${D}${sysconfdir}/rc3.d/S90sja1105.sh
}

SRC_URI += " \
    file://sja1105.sh \
"

FILES_${PN} += "${base_libdir}/*"
FILES_${PN} += "${sysconfdir}/init.d/*"
FILES_${PN} += "${sysconfdir}/rc5.d/*"
FILES_${PN} += "${sysconfdir}/rc3.d/*"

PROVIDES = "kernel-module-sja1105pqrs${KERNEL_MODULE_PACKAGE_SUFFIX}"
RPROVIDES_${PN} = "kernel-module-sja1105pqrs${KERNEL_MODULE_PACKAGE_SUFFIX}"

COMPATIBLE_MACHINE = "s32v234evb|s32v234bbmini"
INHIBIT_PACKAGE_STRIP = "1"

DEPENDS_append = " coreutils-native"
