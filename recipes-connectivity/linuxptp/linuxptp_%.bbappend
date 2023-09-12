PV = "4.1"

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI = "http://sourceforge.net/projects/linuxptp/files/v${PV}/linuxptp-${PV}.tgz \
           file://build-Allow-CC-and-prefix-to-be-overriden-${PV}.patch \
           file://Use-cross-cpp-in-incdefs.patch \
           "

SRC_URI[sha256sum] = "e1743d44f8208897e30895da3579e670ff919b914feb4b5a949f3e421ddde535"
