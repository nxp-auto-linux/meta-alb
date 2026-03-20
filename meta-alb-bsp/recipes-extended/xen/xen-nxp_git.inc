# Copyright 2022, 2024, 2026 NXP

FILESEXTRAPATHS:prepend := "${THISDIR}/xen:"

PV = "4.21"
URL ?= "git://github.com/nxp-auto-linux/xen;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
"

SRCREV = "1217565402723684208fa43d509ef99665ce850d"
LIC_FILES_CHKSUM = "file://COPYING;md5=d1a1e216f80b6d8da95fec897d0dbec9"
