LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://pcie_virt_eth/COPYING;md5=39bba7d2cf0ba1036f2a6e2be52fe3f0"

URL ?= "git://source.codeaurora.org/external/autobsps32/alb-demos;protocol=https"
BRANCH ?= "${RELEASE_BASE}"
SRC_URI = "${URL};branch=${BRANCH}"

S = "${WORKDIR}/git"
SRCREV = "e9ca19e8ebd3f58325990ec738d22b9306b005a4"
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

EXTRA_OEMAKE = "samples=pcie_virt_eth apps="${DEMO_PCIE_APPS}""
