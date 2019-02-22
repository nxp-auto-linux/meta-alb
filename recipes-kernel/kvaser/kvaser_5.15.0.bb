# Copyright 2017,2019 NXP

SUMMARY = "Add support for KVASER USB CAN in BB Mini"
LICENSE = "GPLv2 & BSD"
LIC_FILES_CHKSUM = " \
    file://COPYING.GPL;md5=8c6f370c9073badc24ad75afa6822bcb \
    file://COPYING.BSD;md5=bd8f1ae4e33f4479c3f8a32742cb3c5b \
"

inherit module

VER = "${@d.getVar('PV',True).replace('.', '_')}"
SRC_URI = "http://www.kvaser.com/software/7330130980754/V${VER}/linuxcan.tar.gz"
SRC_URI[md5sum] = "baa38408b1ccea8f3a0e624ca611d399"
SRC_URI[sha256sum] = "95a178395f87ab2e2da78e7de810ef6e08b3b718cfcdd441f7c2936826475cc5"

SRC_URI += "\
	file://kvaser-cross-compile-support.patch \
	file://kvaser-fix-parallel-build.patch \
"

S = "${WORKDIR}/linuxcan"

EXTRA_OEMAKE += 'KERNEL_SOURCE_DIR="${STAGING_KERNEL_DIR}" V=1'

MODULES = "leaf"
# The following modules are untested, to be re-added in the future if needed
#MODULES += "mhydra pcican pcican2 usbcanII virtualcan pciefd usbcanII"

LIBS = "canlib linlib"

RPROVIDES_${PN} = " \
	kernel-module-kvcommon${KERNEL_MODULE_PACKAGE_SUFFIX} \
	kernel-module-leaf${KERNEL_MODULE_PACKAGE_SUFFIX} \
"

# Following modules are untested, to be re-added in the future if needed
#RPROVIDES_${PN} += " \
#	kernel-module-mhydra \
#	kernel-module-pcican \
#	kernel-module-pcican2 \
#	kernel-module-usbcanII \
#	kernel-module-virtualcan \
#	kernel-module-pciefd \
#	kernel-module-usbcanII \
#"

INHIBIT_PACKAGE_STRIP = "1"

# Original do_compile overwrites CC and LD, by stripping the --sysroot flag which
# instructs gcc where to locate required files such as headers.
# As a result, the yocto build may use header files from the host root filesystem.
do_compile() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS

	oe_runmake KERNEL_PATH="${STAGING_KERNEL_DIR}" KERNEL_VERSION="${KERNEL_VERSION}" CC="${CC}" LD="${LD}" AR="${AR}" O="${STAGING_KERNEL_BUILDDIR}" ${MODULES} ${LIBS}
}

MODULES_DIR = "/lib/modules/${KERNEL_VERSION}/kernel/drivers"
KVASER_DIR = "/usr/kvaser"
LIB_DIR = "/lib"
EXAMPLES_BASE = "${KVASER_DIR}/canlib"

do_install() {

	set -x

	mkdir -p "${D}${MODULES_DIR}/usb/misc"
	install -D -m 666 "${S}/common/kvcommon.ko" "${D}${MODULES_DIR}/usb/misc/"

	mkdir -p "${D}${KVASER_DIR}"

	# deploy main config file
	cp "${S}/10-kvaser.rules" "${D}${KVASER_DIR}/"

	# deploy modules and dependencies
	for kvmodule in ${MODULES} ; do
		mkdir -p "${D}${KVASER_DIR}/$kvmodule/"
		cd "${S}/$kvmodule"
		cp *.sh *.ko "${D}${KVASER_DIR}/$kvmodule/"
		if [ -e "$kvmodule" -a -e "$kvmodule.usermap" ] ; then
			cp "$kvmodule" "$kvmodule.usermap" "${D}${KVASER_DIR}/$kvmodule/"
		fi
	done

	mkdir -p "${D}${LIB_DIR}"

	# deploy libs
	for kvlib in ${LIBS} ; do
		find "${S}/$kvlib" -name "lib$kvlib.so*" -execdir cp -P -t "${D}${LIB_DIR}/" {} \+
	done
	find "${D}${LIB_DIR}" -name "lib*.so*" -execdir chmod a+rwx {} \+

	# deploy examples and headers
	cp -P -r "${S}/include" "${D}${KVASER_DIR}/"
	mkdir -p "${D}${EXAMPLES_BASE}"
	cp -P -r "${S}/canlib/examples" "${D}${EXAMPLES_BASE}/"

	set +x
}

do_package_qa() {
	:
}

FILES_${PN} += "${MODULES_DIR}"
FILES_${PN} += "${KVASER_DIR}"
FILES_${PN} += "${EXAMPLES_BASE}"
FILES_${PN} += "${LIB_DIR}"

FILES_${PN}-dev = ""
FILES_${PN}-dbg = ""

ALLOW_EMPTY_${PN}-dev = "1"
ALLOW_EMPTY_${PN}-dbg = "1"
