PV = "4.0"

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI = "http://sourceforge.net/projects/linuxptp/files/v${PV}/linuxptp-${PV}.tgz \
           file://build-Allow-CC-and-prefix-to-be-overriden-${PV}.patch \
           file://Use-cross-cpp-in-incdefs.patch \
           "

SRC_URI[sha256sum] = "d27d5ef296bb3d285e22e69f75ae023b4b42a2f4655130d6d390d8afcbc3d933"
