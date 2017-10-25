# Copyright 2017 NXP
# To bitbake this recipe you should follow the steps from
# the readme file of the yocto layer.
#

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=39bba7d2cf0ba1036f2a6e2be52fe3f0"
SRC_URI = "git://sw-stash.freescale.net/scm/alb/alb-demos.git;branch=develop;protocol=http"

S = "${WORKDIR}/git"
SRCREV = "9a15f519b43aceda6fa5466ced1c92f41ee733b3"
SAMPLESDIR = "/opt/samples"
DESTDIR = "${D}${SAMPLESDIR}"

do_install() {
        install -d ${DESTDIR}
        oe_runmake install INSTALLDIR=${DESTDIR}
}

FILES_${PN} = "${SAMPLESDIR}"
FILES_${PN}-dbg += "${SAMPLESDIR}/.debug"

COMPATIBLE_MACHINE = "s32v234evb|s32v234pcie|s32v234tmdp|s32v234bbmini"
