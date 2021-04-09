# Copyright 2017,2019 NXP

SUMMARY = "Add support for KVASER USB CAN in BB Mini"
LICENSE = "GPLv2 & BSD"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=2cf4d51e36bb1104d17f3f6849f7565e \
    file://COPYING.GPL;md5=4cbb77fd75630b4028ece91b3a627eb4 \
    file://COPYING.BSD;md5=6957ddf8dbb77d424a70a509ee99f569 \
"

inherit module

SRC_URI = "https://www.kvaser.com/download/?utm_ean=7330130980754&utm_status=V${PV};downloadfilename=linuxcan.tar.gz"
SRC_URI[md5sum] = "2eb56959f54bd53e8dc8949630d221ef"
SRC_URI[sha256sum] = "4fdb4bd8d4a4088f212f5892869a81a01372ef9434430c18b36ff52e2c221c01"

SRC_URI += "\
	file://kvaser-fix-parallel-build.patch \
"

S = "${WORKDIR}/linuxcan"

EXTRA_OEMAKE += 'KDIR="${STAGING_KERNEL_DIR}" V=1'
TARGET_CC_ARCH += "-Wno-error=maybe-uninitialized"

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

MODULES_DIR = "${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers"
KVASER_DIR = "/usr/kvaser"
LIB_DIR = "${base_libdir}"
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
