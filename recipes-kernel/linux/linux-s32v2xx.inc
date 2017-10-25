DESCRIPTION = "Linux kernel for S32V2xx platforms"
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel
inherit fsl-kernel-localversion
require recipes-kernel/linux/linux-dtb.inc

SCMVERSION ?= "y"
LOCALVERSION = ""

DEPENDS_append = " libgcc"

KERNEL_CC_append = " ${TOOLCHAIN_OPTIONS}"
KERNEL_LD_append = " ${TOOLCHAIN_OPTIONS}"

S = "${WORKDIR}/git"
do_configure_prepend() {
    # copy desired defconfig so we pick it up for the real kernel_do_configure
    cp ${KERNEL_DEFCONFIG} ${B}/.config

	# During development we want to do easy config overrides
	# add config fragments
	for deltacfg in ${DELTA_KERNEL_DEFCONFIG}; do
		if [ -f "${deltacfg}" ]; then
		    ${S}/scripts/kconfig/merge_config.sh -m .config ${deltacfg}
		elif [ -f "${S}/arch/arm64/configs/${deltacfg}" ]; then
		    ${S}/scripts/kconfig/merge_config.sh -m .config \
		    ${S}/arch/arm64/configs/${deltacfg}
		fi
	done

    # copy .config to defconfig as required by fsl-kernel-localversion
    cp ${B}/.config ${WORKDIR}/defconfig
}

# make everything compatible for the time being
COMPATIBLE_MACHINE_$MACHINE = "$MACHINE"

# ***** uImage Hack START *****
DEPENDS += "u-boot-mkimage-native"
# This is a VERY magic hack to create a uImage from the Image.
# The problem is that the current U-Boot really doesn't know about aarch64
# and therefore does not support the "booti" command yet. We need
# this hack until we have a 2015 U-Boot version with booti support.
UBOOT_ENTRYPOINT ?= "80080000"
UBOOT_LOADADDRESS ?= "${UBOOT_ENTRYPOINT}"
UIMAGE_BASE_NAME ?= "uImage-${PKGE}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
do_install_append() {
	# Part of the uImage hack for S32V234 to create an arm64 uImage for the rootfs
	if test -e arch/${ARCH}/boot/Image ; then
		uboot-mkimage -A ${UBOOT_ARCH} -O linux -T kernel -C none -a ${UBOOT_LOADADDRESS} -e ${UBOOT_ENTRYPOINT} -n "${DISTRO_NAME}/${PV}/${MACHINE}" -d arch/${ARCH}/boot/Image arch/${ARCH}/boot/uImage
		install -m 0644 arch/${ARCH}/boot/uImage ${D}/boot/uImage-${KERNEL_VERSION}
	fi
}
FILES_kernel-image += "/boot/uImage*"
pkg_postinst_kernel-image_append () {
	update-alternatives --install /${KERNEL_IMAGEDEST}/uImage uImage /${KERNEL_IMAGEDEST}/uImage-${KERNEL_VERSION} ${KERNEL_PRIORITY} || true
}

pkg_postrm_kernel-image_append () {
	update-alternatives --remove uImage uImage-${KERNEL_VERSION} || true
}

do_deploy_append() {
	# Part of the uImage hack for S32V234 to put an arm64 uImage into the deploy folder
	install -m 0644 arch/${ARCH}/boot/uImage ${DEPLOYDIR}/${UIMAGE_BASE_NAME}
	cd ${DEPLOYDIR}
	ln -sf ${UIMAGE_BASE_NAME} uImage
	ln -sf ${UIMAGE_BASE_NAME} uImage-${MACHINE}.bin
}
# ***** uImage Hack END *****
