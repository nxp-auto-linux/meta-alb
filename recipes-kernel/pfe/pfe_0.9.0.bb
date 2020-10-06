# Copyright 2019-2020 NXP
#
# This is the PFE driver for Linux kernel 4.19 and 5.4

SUMMARY = "Linux driver for the Packet Forwarding Engine hardware"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE-GPL2.txt;md5=5dcdfe25f21119aa5435eab9d0256af7"

inherit module deploy

# Dummy entry to keep the recipe parser happy if we don't use this recipe
PFE_LOCAL_FIRMWARE_DIR ?= "."

PFE_FW_BIN ?= "s32g_pfe_class.fw"

SRC_URI = "git://source.codeaurora.org/external/autobsps32/extra/pfeng;protocol=https \
	file://${PFE_LOCAL_FIRMWARE_DIR}/${PFE_FW_BIN} \
	"
SRCREV = "0046ccdcc7a70afececdccfca33620f7b08ac88b"

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
FW_INSTALL_NAME ?= "s32g_pfe_class.fw"

EXTRA_OEMAKE_append = " KBUILD_MODPOST_WARN=0 KERNELDIR=${STAGING_KERNEL_DIR} MDIR=${MDIR} -C ${MDIR} V=1 all"

module_do_install() {
	install -D ${MDIR}/pfeng.ko ${INSTALL_DIR}/pfeng.ko
	mkdir -p "${FW_INSTALL_DIR}"
	install -D "${WORKDIR}/${PFE_LOCAL_FIRMWARE_DIR}/${PFE_FW_BIN}" "${FW_INSTALL_DIR}/${FW_INSTALL_NAME}"
}

do_deploy() {
	install -d ${DEPLOYDIR}

	if [ -f ${FW_INSTALL_DIR}/${FW_INSTALL_NAME} ];then
		install -m 0644 ${FW_INSTALL_DIR}/${FW_INSTALL_NAME} ${DEPLOYDIR}/${FW_INSTALL_NAME}
	fi
}

addtask do_deploy after do_install

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

COMPATIBLE_MACHINE = "s32g274aevb|s32g274ardb|s32g274ardb2"
