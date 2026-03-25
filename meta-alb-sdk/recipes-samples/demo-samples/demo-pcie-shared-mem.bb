LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://pcie_shared_mem/LICENSE;md5=39bba7d2cf0ba1036f2a6e2be52fe3f0"

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

DEMO_PCIE_APPS ?= "pcie_ep pcie_rc"

DEMO_PCIE_APPS:ls2 = "pcie_rc"
DEMO_PCIE_APPS:lx2160a = "pcie_rc"

EXTRA_OEMAKE = "samples=pcie_shared_mem apps="${DEMO_PCIE_APPS}""

require demo-pcie.inc
