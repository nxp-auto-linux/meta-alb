SUMMARY = "A tester for BLAS libraries"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a64fe8ed738f87b3d87660bcb20f659e"

DEPENDS = "libopenblas"

SRC_URI = "git://github.com/xianyi/BLAS-Tester.git \
    file://0001-Fix-Makefile-assignment.patch \
"
SRCREV = "8e1f62468ef377c4621dc9dee49f3a4b17134652"

S = "${WORKDIR}/git"

inherit pkgconfig

EXTRA_OEMAKE += "L2SIZE=4194304 NUMTHREADS=4 ARCH=ARM64"
EXTRA_OEMAKE += "TEST_BLAS=${STAGING_LIBDIR}/libopenblas.a"
TARGET_CC_ARCH += "${LDFLAGS}"

FILES:${PN} += "${datadir}/BLAS-Tester"
FILES:${PN}-dbg += "${datadir}/BLAS-Tester/.debug"

do_install() {
	install -d ${D}${datadir}/BLAS-Tester
	cp -r ${S}/bin/* ${D}${datadir}/BLAS-Tester/
}


