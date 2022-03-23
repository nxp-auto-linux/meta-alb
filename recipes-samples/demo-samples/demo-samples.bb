# Copyright 2017,2022 NXP

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.BSD;md5=d1fe458e57ae72e9abc9aff2684690d0"

URL ?= "git://source.codeaurora.org/external/autobsps32/alb-demos;protocol=https"
BRANCH ?= "${RELEASE_BASE}"
SRC_URI = "${URL};branch=${BRANCH}"

S = "${WORKDIR}/git"
SRCREV = "6f0611afd51dadd886560769730bfd9581d133d2"
SAMPLESDIR = "/opt/samples"
DESTDIR = "${D}${SAMPLESDIR}"

do_install() {
        install -d ${DESTDIR}
        oe_runmake install INSTALLDIR=${DESTDIR}
}

FILES_${PN} = "${SAMPLESDIR}"
FILES_${PN}-dbg += "${SAMPLESDIR}/.debug"

DEPENDS += "libgpiod"

# This recipe applies to all non-pcie demos.
# Clustering demo has not been added.
EXTRA_OEMAKE += "samples="multicore gpio_sysfs gpio_libgpiod networking""
