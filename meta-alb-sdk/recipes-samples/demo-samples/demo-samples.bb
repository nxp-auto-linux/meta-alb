# Copyright 2017,2022 NXP

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://networking/LICENSE;md5=d1fe458e57ae72e9abc9aff2684690d0"

URL ?= "git://github.com/nxp-auto-linux/alb-demos;protocol=https"
BRANCH ?= "${RELEASE_BASE}"
SRC_URI = "${URL};branch=${BRANCH}"
FOLLOWER_RB = "1"

S = "${WORKDIR}/git"
SRCREV = "582a7f832c26e43d363f261460b81b7dedccabfc"
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
