LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://pcie_virt_eth/COPYING;md5=39bba7d2cf0ba1036f2a6e2be52fe3f0"
SRC_URI = "git://source.codeaurora.org/external/autobsps32/alb-demos;branch=alb/master;protocol=https"

S = "${WORKDIR}/git"
SRCREV = "96cc6ca7a5987d83a67b00908d64ff4160889fb1"
SAMPLESDIR = "/opt/samples"
DESTDIR = "${D}${SAMPLESDIR}"

do_install() {
        install -d ${DESTDIR}
        oe_runmake install INSTALLDIR=${DESTDIR}
}

FILES_${PN} = "${SAMPLESDIR}"
FILES_${PN}-dbg += "${SAMPLESDIR}/.debug"

EXTRA_OEMAKE_ls2080abluebox = "samples=pcie_virt_eth S32V_BOARD_SETUP=PCIE_SHMEM_BLUEBOX apps=net_rc"
EXTRA_OEMAKE_ls2084abbmini = "samples=pcie_virt_eth S32V_BOARD_SETUP=PCIE_SHMEM_BLUEBOXMINI apps=net_rc"
EXTRA_OEMAKE_s32v234pcie = "samples=pcie_virt_eth S32V_BOARD_SETUP=PCIE_SHMEM_BLUEBOX apps=net_ep"
EXTRA_OEMAKE_s32v234bbmini = "samples=pcie_virt_eth S32V_BOARD_SETUP=PCIE_SHMEM_BLUEBOXMINI apps=net_ep"

# we only support BlueBox (s32v234pcie and ls2080abluebox) and BlueBox Mini (s32v234bbmini and ls2084abbmini)
COMPATIBLE_MACHINE = "s32v234pcie|s32v234bbmini|ls2080abluebox|ls2084abbmini"
