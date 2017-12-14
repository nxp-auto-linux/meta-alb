# Copyright 2017 NXP
# To bitbake this recipe you should follow the steps from
# the readme file of the yocto layer.
#

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.BSD;md5=d1fe458e57ae72e9abc9aff2684690d0"
SRC_URI = "git://source.codeaurora.org/external/autobsps32/alb-demos;branch=alb/master;protocol=https"

SRC_URI[md5sum] = "fb105258940e1a6730f8db534b2f9b37"

S = "${WORKDIR}/git"
SRCREV = "513abf8935fe1da384256a1552d340b9d8f952f6"
SAMPLESDIR = "/opt/samples"
DESTDIR = "${D}${SAMPLESDIR}"

do_install() {
        install -d ${DESTDIR}
        oe_runmake install INSTALLDIR=${DESTDIR}
}

FILES_${PN} = "${SAMPLESDIR}"
FILES_${PN}-dbg += "${SAMPLESDIR}/.debug"

COMPATIBLE_MACHINE = "s32v234evb|s32v234pcie|s32v234tmdp|s32v234bbmini"
