# Copyright 2022,2024 NXP

FILESEXTRAPATHS:prepend := "${THISDIR}/xen:"

PV = "4.18"
URL ?= "git://github.com/nxp-auto-linux/xen;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
    file://0001-menuconfig-mconf-cfg-Allow-specification-of-ncurses-location.patch \
    file://0001-python-pygrub-pass-DISTUTILS-xen-4.18.patch \
"

SRCREV = "e4f37269c334223dc9c508d0e88296ef35a23f6e"
LIC_FILES_CHKSUM = "file://COPYING;md5=d1a1e216f80b6d8da95fec897d0dbec9"
