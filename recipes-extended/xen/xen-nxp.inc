FILESEXTRAPATHS_prepend := "${THISDIR}/xen:"

PV = "4.14"
URL ?= "git://source.codeaurora.org/external/autobsps32/xen;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
    file://0001-menuconfig-mconf-cfg-Allow-specification-of-ncurses-location.patch \
"

SRCREV = "6c46c30c717b8484fb3b7f6e7c2af9b2bbf649f6"
