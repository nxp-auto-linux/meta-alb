DESCRIPTION = "Proj library for converting between geographic and projection coordinates"
AUTHOR = "Gerald Evenden and Frank Warmerdam"
HOMEPAGE = "http://proj4.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=74d9aaec5fa0cd734341e8c4dc91b608"
FILES:${PN} += "/usr/share/*"

SRC_URI = "http://download.osgeo.org/proj/proj-${PV}.tar.gz"
SRC_URI[md5sum] = "3cbb2a964fd19a496f5f4265a717d31c"
SRC_URI[sha256sum] = "fca0388f3f8bc5a1a803d2f6ff30017532367992b30cf144f2d39be88f36c319"

S = "${WORKDIR}/proj-${PV}"

inherit cmake

do_install() {
        oe_runmake DESTDIR=${D} PREFIX=${prefix} install
}

# On powerpc64, libraries reside in .../lib64
EXTRA_OECMAKE = "-DPROJ_LIB_SUBDIR=${baselib}"

OECMAKE_GENERATOR = "Unix Makefiles"
