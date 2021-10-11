LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://pcie_virt_eth/COPYING;md5=39bba7d2cf0ba1036f2a6e2be52fe3f0"

URL ?= "git://source.codeaurora.org/external/autobsps32/alb-demos;protocol=https"
BRANCH ?= "${RELEASE_BASE}"
SRC_URI = "${URL};branch=${BRANCH}"

S = "${WORKDIR}/git"
SRCREV = "f8d009039895b4bda32f27b6e91a67c53f6b9e8b"
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
DEMO_PCIE_APPS_lx2160a = "pcie_rc"
DEMO_PCIE_APPS_s32v234pcie = "pcie_ep"
DEMO_PCIE_APPS_s32v234bbmini = "pcie_ep"
DEMO_PCIE_APPS_s32v234campp = "pcie_ep"
DEMO_PCIE_APPS_s32v234hpcsom = "pcie_ep"

EXTRA_OEMAKE = "samples=pcie_shared_mem apps="${DEMO_PCIE_APPS}""

require demo-pcie.inc
