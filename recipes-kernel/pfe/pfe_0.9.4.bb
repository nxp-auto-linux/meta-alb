# Copyright 2019-2021 NXP
#
# This is the PFE driver for Linux kernel 5.4 and 5.10

SUMMARY = "Linux driver for the Packet Forwarding Engine hardware"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE-GPL2.txt;md5=5dcdfe25f21119aa5435eab9d0256af7"

inherit module deploy

# Dummy entry to keep the recipe parser happy if we don't use this recipe
NXP_FIRMWARE_LOCAL_DIR ?= "."

PFE_FW_CLASS_BIN ?= "s32g_pfe_class.fw"
PFE_FW_UTIL_BIN ?= "s32g_pfe_util.fw"

URL ?= "git://source.codeaurora.org/external/autobsps32/extra/pfeng;protocol=https"
SRC_URI = "${URL} \
	file://${NXP_FIRMWARE_LOCAL_DIR}/${PFE_FW_CLASS_BIN} \
	file://${NXP_FIRMWARE_LOCAL_DIR}/${PFE_FW_UTIL_BIN} \
	file://0001-AAVB-3196-pfeng-Enable-pfe-eth-netdevs-to-be-selecte.patch \
	file://0002-AAVB-3196-pfeng-Account-for-the-8021q-DSA-tag-length.patch \
	file://0003-AAVB-3196-pfe_platform-Add-4B-margin-to-the-max-rx-p.patch \
	file://0001-phylink-fix-build-with-backported-paches-on-5.4.patch \
	"
SRCREV ?= "3a548033d5126c9354a8529903d0842769718b1e"

# Tell yocto not to bother stripping our binaries, especially the firmware
# since 'aarch64-fsl-linux-strip' fails with error code 1 when parsing the firmware
# ("Unable to recognise the format of the input file")
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

S = "${WORKDIR}/git"
MDIR = "${S}/sw/linux-pfeng"
INSTALL_DIR = "${D}/${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/net/ethernet/nxp/pfe"
FW_INSTALL_DIR = "${D}/${base_libdir}/firmware"
FW_INSTALL_CLASS_NAME ?= "s32g_pfe_class.fw"
FW_INSTALL_UTIL_NAME ?= "s32g_pfe_util.fw"

EXTRA_OEMAKE_append = " KERNELDIR=${STAGING_KERNEL_DIR} MDIR=${MDIR} -C ${MDIR} V=1 drv-build"

# Build PFE for both 1.1 and 2.0 SoC revision
# The user can choose to build specific version only by overwriting this variable
# in this file or in conf/local.conf
# For example, to build only for Rev 2.0, set PFE_SUPPORTED_REV = "2.0"
PFE_SUPPORTED_REV ?= "1.1 2.0"

module_do_compile() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS

	for rev in ${PFE_SUPPORTED_REV}; do

		# convert SoC revision to PFE revision
		if [ "${rev}" = "1.1" ]; then
			pfe_rev="PFE_CFG_IP_VERSION_NPU_7_14"
		elif [ "${rev}" = "2.0" ]; then
			pfe_rev="PFE_CFG_IP_VERSION_NPU_7_14a"
		else
			bberror "Cannont convert '${rev}' to a PFE revision"
		fi

		# standard module build, but setting PFE_CFG_IP_VERSION
		oe_runmake PFE_CFG_IP_VERSION="${pfe_rev}" \
		KERNEL_PATH=${STAGING_KERNEL_DIR}   \
		KERNEL_VERSION=${KERNEL_VERSION}    \
		CC="${KERNEL_CC}" LD="${KERNEL_LD}" \
		AR="${KERNEL_AR}" \
		O=${STAGING_KERNEL_BUILDDIR} \
		KBUILD_EXTRA_SYMBOLS="${KBUILD_EXTRA_SYMBOLS}" \
		${MAKE_TARGETS}

		# all revisions are installed, set them with revision suffix
		mv "${MDIR}/pfeng.ko" "${MDIR}/pfeng-${rev}.ko"
	done
}

module_do_install() {
	# install all supported revisions
	for rev in ${PFE_SUPPORTED_REV}; do
		install -D "${MDIR}/pfeng-${rev}.ko" "${INSTALL_DIR}/pfeng-${rev}.ko"
	done

	install -d "${FW_INSTALL_DIR}"
	install -D "${WORKDIR}/${NXP_FIRMWARE_LOCAL_DIR}/${PFE_FW_CLASS_BIN}" "${FW_INSTALL_DIR}/${FW_INSTALL_CLASS_NAME}"
	install -D "${WORKDIR}/${NXP_FIRMWARE_LOCAL_DIR}/${PFE_FW_UTIL_BIN}" "${FW_INSTALL_DIR}/${FW_INSTALL_UTIL_NAME}"
}

do_deploy() {
	install -d ${DEPLOYDIR}

	if [ -f ${FW_INSTALL_DIR}/${FW_INSTALL_CLASS_NAME} ];then
		install -m 0644 ${FW_INSTALL_DIR}/${FW_INSTALL_CLASS_NAME} ${DEPLOYDIR}/${FW_INSTALL_CLASS_NAME}
	fi

	if [ -f ${FW_INSTALL_DIR}/${FW_INSTALL_UTIL_NAME} ];then
		install -m 0644 ${FW_INSTALL_DIR}/${FW_INSTALL_UTIL_NAME} ${DEPLOYDIR}/${FW_INSTALL_UTIL_NAME}
	fi
}

addtask do_deploy after do_install

# do_package_qa throws error "QA Issue: Architecture did not match"
# when checking the firmware
do_package_qa[noexec] = "1"
do_package_qa_setscene[noexec] = "1"

FILES_${PN} += "${base_libdir}/*"
FILES_${PN} += "${sysconfdir}/modules-load.d/*"


python () {
    # Allow providing one or more modules based on PFE_SUPPORTED_REV
    # Set the needed variables depending on user defined PFE_SUPPORTED_REV

    suffix = (d.getVar('KERNEL_MODULE_PACKAGE_SUFFIX', True) or "")
    revs = d.getVar('PFE_SUPPORTED_REV', True).split()
    pn = (d.getVar('PN', True) or "")

    for rev in revs:
        s = " kernel-module-pfeng-%s%s" % (rev, suffix)

        provides = (d.getVar('PROVIDES', True) or "")
        d.setVar('PROVIDES', provides + s)

        rprovides = (d.getVar('RPROVIDES_%s' % pn, True) or "")
        d.setVar('RPROVIDES_%s' % pn, rprovides + s)
}

# Exclude (R)PROVIDES for the following tasks otherwise a hashbase mismatch is
# reported between the hash before kernel build (which triggers an empty kernel
# version in the (R)PROVIDES) and the hash after kernel build (using the updated
# kernel version)
do_populate_sysroot[vardepsexclude] += "PROVIDES"
do_populate_sysroot_setscene[vardepsexclude] += "PROVIDES"
do_package[vardepsexclude] += "PROVIDES RPROVIDES_${PN}"

COMPATIBLE_MACHINE = "s32g2"
