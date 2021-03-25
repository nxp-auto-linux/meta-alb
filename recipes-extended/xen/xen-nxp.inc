FILESEXTRAPATHS_prepend := "${THISDIR}/xen:"

PV = "4.14"
URL ?= "git://source.codeaurora.org/external/autobsps32/xen;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
    file://xen-arm64-implement-atomic-fetch-add.patch \
    file://0001-menuconfig-mconf-cfg-Allow-specification-of-ncurses-location.patch \
    file://0001-python-pygrub-pass-DISTUTILS-xen-4.14.patch \
    file://0001-xen-build-temporarily-inhibit-Werror-4.14.patch \
"

SRCREV = "daf8e7df62eaed5bbe2cf257ec5265747a6316a8"
