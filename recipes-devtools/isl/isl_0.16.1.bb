require isl.inc

DEPENDS = "gmp"

LIC_FILES_CHKSUM = "file://LICENSE;md5=0c7c9ea0d2ff040ba4a25afa0089624b"

SRC_URI = "http://isl.gforge.inria.fr/isl-${PV}.tar.gz"

SRC_URI[md5sum] = "a492b1c5bdeb3dda6bb25af4ee8eedfa"
SRC_URI[sha256sum] = "fb34703bd694622aef92164bad983c15ec04274edbe19e614d9ecda54b85603d"

S = "${WORKDIR}/isl-${PV}"
