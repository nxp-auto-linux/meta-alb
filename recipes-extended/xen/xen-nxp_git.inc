# Copyright 2022 NXP

FILESEXTRAPATHS:prepend := "${THISDIR}/xen:"

URL ?= "git://github.com/nxp-auto-linux/xen;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
"
