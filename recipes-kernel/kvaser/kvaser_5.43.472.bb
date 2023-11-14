# Copyright 2017,2019 NXP

SUMMARY = "Add support for KVASER USB CAN in BB Mini"
LICENSE = "GPL-2.0-only & BSD"

LIC_FILES_CHKSUM = " \
    file://COPYING;md5=df8cdeaa1ac5c05e71624f0446dbec13 \
    file://COPYING.GPL;md5=4cbb77fd75630b4028ece91b3a627eb4 \
    file://COPYING.BSD;md5=6957ddf8dbb77d424a70a509ee99f569 \
"

inherit module

SRC_URI = "https://www.kvaser.com/download/?utm_ean=7330130980754&utm_status=V${PV};downloadfilename=linuxcan.tar.gz"
SRC_URI[md5sum] = "2a691eeb9761fa60e6d557cdd33fad56"
SRC_URI[sha256sum] = "8de633774c259053d431ee8b87fc85a5ccca69041cd83fdba3b8f6dcf708f2cb"

S = "${WORKDIR}/linuxcan"

EXTRA_OEMAKE += 'KDIR="${STAGING_KERNEL_DIR}" V=1'

MODULES = "leaf"
# The following modules are untested, to be re-added in the future if needed
#MODULES += "mhydra pcican pcican2 usbcanII virtualcan pciefd usbcanII"

LIBS = "canlib linlib"

RPROVIDES:${PN} = " \
	kernel-module-kvcommon${KERNEL_MODULE_PACKAGE_SUFFIX} \
	kernel-module-leaf${KERNEL_MODULE_PACKAGE_SUFFIX} \
"

# Following modules are untested, to be re-added in the future if needed
#RPROVIDES:${PN} += " \
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

	# deploy modules and dependencies
	for kvmodule in ${MODULES} ; do
		mkdir -p "${D}${KVASER_DIR}/$kvmodule/"
		cd "${S}/$kvmodule"
		# copy manual install scripts and the corresponding kernel module
		cp *.sh "${D}${KVASER_DIR}/$kvmodule/"
		cp $kvmodule.ko "${D}${MODULES_DIR}/usb/misc/"
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

FILES:${PN} += "${MODULES_DIR}"
FILES:${PN} += "${KVASER_DIR}"
FILES:${PN} += "${EXAMPLES_BASE}"
FILES:${PN} += "${LIB_DIR}"

module_conf_kvaser_usb = "blacklist kvaser_usb"
module_conf_leaf = "softdep leaf post: kvcommon"
KERNEL_MODULE_PROBECONF += "kvcommon ${MODULES}"
KERNEL_MODULE_AUTOLOAD += "kvcommon ${MODULES}"

FILES:${PN}-dev = ""
FILES:${PN}-dbg = ""
FILES:${PN} += "${sysconfdir}/modules-load.d/*"

ALLOW_EMPTY:${PN}-dev = "1"
ALLOW_EMPTY:${PN}-dbg = "1"
