# Copyright 2017 NXP

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.BSD;md5=d1fe458e57ae72e9abc9aff2684690d0"

URL ?= "git://source.codeaurora.org/external/autobsps32/alb-demos;protocol=https"
BRANCH ?= "${RELEASE_BASE}"
SRC_URI = "${URL};branch=${BRANCH}"

S = "${WORKDIR}/git"
SRCREV = "e278f9622870a525e99c76c8722d04eb63a7033e"
SAMPLESDIR = "/opt/samples"
DESTDIR = "${D}${SAMPLESDIR}"

do_install() {
        install -d ${DESTDIR}
        oe_runmake install INSTALLDIR=${DESTDIR}
}

FILES_${PN} = "${SAMPLESDIR}"
FILES_${PN}-dbg += "${SAMPLESDIR}/.debug"

# This recipe applies to all non-pcie demos.
# Clustering demo has not been added.
EXTRA_OEMAKE += "samples="multicore gpio networking""

COMPATIBLE_MACHINE = "s32v234evb|s32v234pcie|s32v234tmdp|s32v234bbmini|s32v234sbc"
