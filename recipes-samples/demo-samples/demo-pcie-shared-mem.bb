LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://pcie_virt_eth/COPYING;md5=39bba7d2cf0ba1036f2a6e2be52fe3f0"

URL ?= "git://source.codeaurora.org/external/autobsps32/alb-demos;protocol=https"
BRANCH ?= "${RELEASE_BASE}"
SRC_URI = "${URL};branch=${BRANCH}"

S = "${WORKDIR}/git"
SRCREV = "264a88d49aff18a2739351c8f53fc279ae5c9577"
SAMPLESDIR = "/opt/samples"
DESTDIR = "${D}${SAMPLESDIR}"

do_install() {
        install -d ${DESTDIR}
        oe_runmake install INSTALLDIR=${DESTDIR}
}

FILES_${PN} = "${SAMPLESDIR}"
FILES_${PN}-dbg += "${SAMPLESDIR}/.debug"

DEMO_PCIE_APPS ?= "pcie_ep pcie_rc"

DEMO_PCIE_APPS_ls2 = "pcie_rc"
DEMO_PCIE_APPS_s32v234pcie = "pcie_ep"
DEMO_PCIE_APPS_s32v234bbmini = "pcie_ep"

EXTRA_OEMAKE = "samples=pcie_shared_mem apps="${DEMO_PCIE_APPS}""

require demo-pcie.inc

# we support BlueBox (s32v234pcie and ls2080abluebox) BlueBox Mini (s32v234bbmini and ls2084abbmini) and s32v234evb
COMPATIBLE_MACHINE = "s32v234pcie|s32v234evb|s32v234bbmini|ls2080abluebox|ls2084abbmini|s32v234sbc|s32v234campp"
