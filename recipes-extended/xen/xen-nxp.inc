FILESEXTRAPATHS_prepend := "${THISDIR}/xen:"

PV = "4.14"
URL ?= "git://source.codeaurora.org/external/autobsps32/xen;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
    file://0001-menuconfig-mconf-cfg-Allow-specification-of-ncurses-location.patch \
    file://0001-python-pygrub-pass-DISTUTILS-xen-4.14.patch \
    file://0001-xen-build-temporarily-inhibit-Werror-4.14.patch \
"

SRCREV = "becdd18e506c097c69bf73aa642b0bc21af8d280"
