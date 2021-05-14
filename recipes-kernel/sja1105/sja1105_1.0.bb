# Copyright 2017-2020 NXP

SUMMARY = "Add support for SJA1105 switch for S32V234EVB, BB Mini, S32G-PROCEVB-S plus S32GRV-PLATEVB and S32G-VNP-RDB"
LICENSE = "GPLv2+ & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit module

URL ?= "git://source.codeaurora.org/external/autobsps32/sja1105x;protocol=https"
BRANCH ?= "${RELEASE_BASE}"
SRC_URI = "${URL};branch=${BRANCH}"
SRCREV = "573ecec64167cf01e4e8451734f92382952f772c"

KERNEL_NAME = "${PREFERRED_PROVIDER_virtual/kernel}"
KERNEL_VER = '${@d.getVar("PREFERRED_VERSION_${KERNEL_NAME}",True)}'
MAJ_VER = '${@oe.utils.trim_version("${KERNEL_VER}",1)}'

# Customizations for some kernel versions
OTHER_KERNEL_INCLUDE = "sja1105-append-${MAJ_VER}.x.inc"

include ${OTHER_KERNEL_INCLUDE}

S = "${WORKDIR}/git"
DESTDIR = "${D}"
EXTRA_OEMAKE_append = " INSTALL_DIR=${DESTDIR} KERNELDIR=${KBUILD_OUTPUT} MYCOMPILER=${CROSS_COMPILE}gcc "
EXTRA_OEMAKE_append_s32v234evb = " MYPLATFORM=evb "
EXTRA_OEMAKE_append_s32v234bbmini = " MYPLATFORM=bbmini "
EXTRA_OEMAKE_append_s32g2evb = " MYPLATFORM=gplat "
EXTRA_OEMAKE_append_s32g274ardb = " MYPLATFORM=rdb "

KERNEL_MODULE_AUTOLOAD += "sja1105pqrs"

FILES_${PN} += "${base_libdir}/*"
FILES_${PN} += "${sysconfdir}/modules-load.d/*"

PROVIDES = "kernel-module-sja1105pqrs${KERNEL_MODULE_PACKAGE_SUFFIX}"
RPROVIDES_${PN} = "kernel-module-sja1105pqrs${KERNEL_MODULE_PACKAGE_SUFFIX}"

COMPATIBLE_MACHINE = "s32v234evb|s32v234bbmini|s32g2evb|s32g274ardb"
INHIBIT_PACKAGE_STRIP = "1"

DEPENDS_append = " coreutils-native"
