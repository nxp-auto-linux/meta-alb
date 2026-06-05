# Copyright 2022, 2024, 2026 NXP

FILESEXTRAPATHS:prepend := "${THISDIR}/xen:"

PV = "4.21"
URL ?= "git://github.com/nxp-auto-linux/xen;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
"

SRCREV = "796cafd61c08b98149b91a3718646689a0f304c5"
LIC_FILES_CHKSUM = "file://COPYING;md5=d1a1e216f80b6d8da95fec897d0dbec9"
