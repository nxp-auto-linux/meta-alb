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

SRCREV = "f340f108f769cb2cd15eedc95a90255d6375925c"
