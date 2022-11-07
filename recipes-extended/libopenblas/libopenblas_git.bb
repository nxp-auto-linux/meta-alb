SUMMARY = "An optimized BLAS library"
HOMEPAGE = "http://www.openblas.net"
BUGTRACKER = "http://www.openblas.net"
SECTION = "devel"
LICENSE = "OpenBLAS"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5adf4792c949a00013ce25d476a2abc0"

DEPENDS = "libgfortran"

PV = "0.2.18"

SRC_URI = "git://github.com/xianyi/OpenBLAS.git \
    file://fixlibnameforyocto.patch \
"
SRCREV = "12ab1804b6ebcd38b26960d65d254314d8bc33d6"

S = "${WORKDIR}/git"

inherit pkgconfig
EXTRA_OEMAKE += 'HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}"'
EXTRA_OEMAKE += "OPENBLAS_INCLUDE_DIR=${includedir}"
EXTRA_OEMAKE += "OPENBLAS_LIBRARY_DIR=${libdir}"
EXTRA_OEMAKE += "OPENBLAS_BINARY_DIR=${bindir}"

# Experimental!
EXTRA_OEMAKE:append:aarch64 = " TARGET=ARMV8"
EXTRA_OEMAKE:append:powerpc64 = " TARGET=PPC970"

FILES:${PN}-dev += "${libdir}/cmake/openblas"

do_install() {
        oe_runmake DESTDIR=${D} PREFIX=${prefix} install

        rmdir ${D}${bindir}
}

