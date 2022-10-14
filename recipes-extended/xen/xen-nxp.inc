FILESEXTRAPATHS_prepend := "${THISDIR}/xen:"

PV = "4.14"
URL ?= "git://github.com/nxp-auto-linux/xen;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
    file://0001-menuconfig-mconf-cfg-Allow-specification-of-ncurses-location.patch \
    file://0001-python-pygrub-pass-DISTUTILS-xen-4.14.patch \
    file://0001-xen-build-temporarily-inhibit-Werror-4.14.patch \
"

SRCREV = "2dc967c6b291a18844ea6f85105a3fc392eeb525"
