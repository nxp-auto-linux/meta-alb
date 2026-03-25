# Copyright 2017,2022 NXP

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://networking/LICENSE;md5=d1fe458e57ae72e9abc9aff2684690d0"

URL ?= "git://github.com/nxp-auto-linux/alb-demos;protocol=https"
BRANCH ?= "${RELEASE_BASE}"
SRC_URI = "${URL};branch=${BRANCH}"

S = "${WORKDIR}/git"
SRCREV = "b166ca4b3c73a1d3e72aed5c75a0e4e76e420311"
SAMPLESDIR = "/opt/samples"
DESTDIR = "${D}${SAMPLESDIR}"

do_install() {
        install -d ${DESTDIR}
        oe_runmake install INSTALLDIR=${DESTDIR}
}

FILES:${PN} = "${SAMPLESDIR}"
FILES:${PN}-dbg += "${SAMPLESDIR}/.debug"

DEPENDS += "libgpiod"

# This recipe applies to all non-pcie, non-tee demos.
# Clustering demo has not been added.
EXTRA_OEMAKE += "samples="multicore gpio_libgpiod networking""
