DESCRIPTION = "LibFCI Example: Command line tool for configuration of PFE"
HOMEPAGE = "https://github.com/nxp-auto-linux/pfeng"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE-BSD3.txt;md5=6b674f4e7c2e72a1907ad7a7f03b800c"

PR = "r0"

URL ?= "git://github.com/nxp-auto-linux/pfeng;protocol=https"
SRC_URI = "${URL}"
SRCREV = "0387b32f5919459d176e4887b2869e0261e34c11"

S = "${WORKDIR}/git"
MDIR = "${S}/sw/libfci_cli"

# Workaround for makefile.
# The makefile is unfortunately not properly prepared to be ran by YOCTO (no option to provide sysroot for toolchain).
# Therefore, the sysroot is prepended to "CCFLAGS_all" for compiler, and to "LDFLAGS_all" for linker.
# Those symbols are recognized by the makefile and should not collide with YOCTO symbols.
CCFLAGS_all = "--sysroot=\"${STAGING_DIR_HOST}\""
LDFLAGS_all = "--sysroot=\"${STAGING_DIR_HOST}\""
SYSROOT_WORKAROUND = "CCFLAGS_all=${CCFLAGS_all} LDFLAGS_all=${LDFLAGS_all}"

CFLAGS_prepend = "-I${S} "

PACKAGES = "${PN} ${PN}-dbg"
FILES_${PN} += "${bindir}/libfci_cli"

RDEPENDS_${PN} = "pfe"
RDEPENDS_${PN}-dbg = "pfe"

do_compile() {
	cd ${MDIR}
	${SYSROOT_WORKAROUND} ${MAKE} TARGET_OS=LINUX PLATFORM=${TARGET_SYS} all
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${MDIR}/libfci_cli ${D}${bindir}
}

COMPATIBLE_MACHINE = "s32g"
