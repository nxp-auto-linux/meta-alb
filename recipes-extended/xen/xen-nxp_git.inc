# Copyright 2022 NXP

FILESEXTRAPATHS:prepend := "${THISDIR}/xen:"

PV = "4.17"
URL ?= "git://github.com/nxp-auto-linux/xen;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
    file://0001-menuconfig-mconf-cfg-Allow-specification-of-ncurses-location.patch \
    file://0001-python-pygrub-pass-DISTUTILS-xen-4.15.patch \
"

SRCREV = "4fc1bf32a2f50a6635050fc9d1a5583f39952632"
LIC_FILES_CHKSUM = "file://COPYING;md5=d1a1e216f80b6d8da95fec897d0dbec9"
