LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://pcie_virt_eth/COPYING;md5=39bba7d2cf0ba1036f2a6e2be52fe3f0"

URL ?= "git://source.codeaurora.org/external/autobsps32/alb-demos;protocol=https"
BRANCH ?= "${RELEASE_BASE}"
SRC_URI = "${URL};branch=${BRANCH}"

S = "${WORKDIR}/git"
SRCREV = "e67343933c4a4ce7d63b459678bcec0825acb8bc"
SAMPLESDIR = "/opt/samples"
DESTDIR = "${D}${SAMPLESDIR}"

do_install() {
        install -d ${DESTDIR}
        oe_runmake install INSTALLDIR=${DESTDIR}
}

FILES_${PN} = "${SAMPLESDIR}"
FILES_${PN}-dbg += "${SAMPLESDIR}/.debug"

DEMO_PCIE_APPS ?= "net_ep net_rc"

DEMO_PCIE_APPS_ls2 = "net_rc"
DEMO_PCIE_APPS_lx2160a = "net_rc"
DEMO_PCIE_APPS_s32v234pcie = "net_ep"
DEMO_PCIE_APPS_s32v234bbmini = "net_ep"
DEMO_PCIE_APPS_s32v234campp = "net_ep"
DEMO_PCIE_APPS_s32v234hpcsom = "net_ep"

EXTRA_OEMAKE = "samples=pcie_virt_eth apps="${DEMO_PCIE_APPS}""

require demo-pcie.inc
