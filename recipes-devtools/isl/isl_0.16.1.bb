require isl.inc

DEPENDS = "gmp"

LIC_FILES_CHKSUM = "file://LICENSE;md5=0c7c9ea0d2ff040ba4a25afa0089624b"

SRC_URI = "https://gcc.gnu.org/pub/gcc/infrastructure/isl-${PV}.tar.bz2"

SRC_URI[md5sum] = "ac1f25a0677912952718a51f5bc20f32"
SRC_URI[sha256sum] = "412538bb65c799ac98e17e8cfcdacbb257a57362acfaaff254b0fcae970126d2"


S = "${WORKDIR}/isl-${PV}"
