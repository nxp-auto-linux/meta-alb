# Copyright 2018 NXP
#
# This is the GMAC driver for Linux kernel 4.1

SUMMARY = "Linux driver for transmitting and receiving Ethernet frames using the GMAC hardware"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://Makefile;endline=59;md5=02ed8f1463871c0a4d6831afa052f227"

inherit module

SRC_URI = "git://source.codeaurora.org/external/autobsps32/extra/gmac;protocol=https"
SRCREV = "83cad93900b8e3b515df4a8b4cf184db52132fc5"

S = "${WORKDIR}/git/driver"
EXTRA_OEMAKE_append = " KDIR=${STAGING_KERNEL_DIR} "

module_do_install() {
	install -D dwc-eqos.ko ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/net/ethernet/gmac/dwc-eqos.ko
}

KERNEL_MODULE_AUTOLOAD += "dwc-eqos"

FILES_${PN} += "${base_libdir}/*"
FILES_${PN} += "${sysconfdir}/modules-load.d/*"

PROVIDES = "kernel-module-dwc-eqos${KERNEL_MODULE_PACKAGE_SUFFIX}"
RPROVIDES_${PN} = "kernel-module-dwc-eqos${KERNEL_MODULE_PACKAGE_SUFFIX}"

COMPATIBLE_MACHINE = "gen1"
