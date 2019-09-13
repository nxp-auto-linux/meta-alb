# Copyright 2019 NXP
#
# This is the PFE driver for Linux kernel 4.14 and 4.19

SUMMARY = "Linux driver for the Packet Forwarding Engine hardware"
LICENSE = "Freescale-EULA"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=116de28c37181b72b36566106a941904"

inherit module

# Dummy entry to keep the recipe parser happy if we don't use this recipe
PFE_LOCAL_FIRMWARE_DIR ?= "."

PFE_FW_BIN ?= "class_s32g.elf"

SRC_URI = " \
	git://source.codeaurora.org/external/autobsps32/extra/pfeng;protocol=https \
	file://${PFE_LOCAL_FIRMWARE_DIR}/${PFE_FW_BIN} \
	"
SRCREV = "cd482abb1e882b2576137ff8d2283e97881f96f7"

# Tell yocto not to bother stripping our binaries, especially the firmware
# since 'aarch64-fsl-linux-strip' fails with error code 1 when parsing the firmware
# ("Unable to recognise the format of the input file")
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

S = "${WORKDIR}/git"
MDIR = "${S}/sw/linux-pfeng"
INSTALL_DIR = "${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/net/ethernet/nxp/pfe"
FW_INSTALL_DIR = "${D}/lib/firmware"
FW_INSTALL_NAME ?= "pfe-s32g-class.fw"

EXTRA_OEMAKE_append = " KERNELDIR=${STAGING_KERNEL_DIR} MDIR=${MDIR} -C ${MDIR} V=1 all"

module_do_install() {
	install -D ${MDIR}/pfeng.ko ${INSTALL_DIR}/pfeng.ko
	mkdir -p "${FW_INSTALL_DIR}"
	install -D "${WORKDIR}/${PFE_LOCAL_FIRMWARE_DIR}/${PFE_FW_BIN}" "${FW_INSTALL_DIR}/${FW_INSTALL_NAME}"
}

# do_package_qa throws error "QA Issue: Architecture did not match"
# when checking the firmware
do_package_qa[noexec] = "1"
do_package_qa_setscene[noexec] = "1"

FILES_${PN} += "${base_libdir}/*"
FILES_${PN} += "${sysconfdir}/modules-load.d/*"

PROVIDES = " \
	kernel-module-pfeng${KERNEL_MODULE_PACKAGE_SUFFIX} \
	"
RPROVIDES_${PN} = " \
	kernel-module-pfeng${KERNEL_MODULE_PACKAGE_SUFFIX} \
	"

COMPATIBLE_MACHINE = "s32g274aevb"
