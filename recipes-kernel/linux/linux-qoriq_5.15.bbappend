FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

COMMONSRC_URI = " \
    file://0001-kernel-Added-BB-Mini-device-tree.patch \
    file://0001-linux-qoriq-Specific-DTS-for-the-LS2084A-based-BlueB.patch \
    file://0001-linux-qoriq-BlueBox-1-2-dts-was-broken-for-Eth-PHYs.patch \
    file://0001-linux-qoriq-dts-support-for-BlueBox3.patch \
\
    file://blueboxconfig \
    file://binfmt_misc.cfg \
    file://enablepktgen.cfg \
    file://iptables.cfg \
    file://iso9660.cfg \
    file://enableusbcan.cfg \
    file://containers.config \
    file://lxc.cfg \
    file://docker.cfg \
    file://bpfcgroup.cfg \
"

SRC_URI:append:ls2 = " \
    ${COMMONSRC_URI} \
    file://ls2blueboxconfig \
    file://enablegfxhwls2.cfg \
"

SRC_URI:append:ls2084abbmini = " \
    file://gpio.cfg \
"

# The BlueBox 1 uses a Cortina PHY.
SRC_URI:append:ls2084abluebox = " \
    file://enablecortina.cfg \
"

# Optional to simplify Ethernet debug
SRC_URI:append:ls2 = " \
    file://dpaa2debug.cfg \
"

SRC_URI:append:lx2160abluebox3 = " \
    ${COMMONSRC_URI} \
    file://enablegfxhwls2.cfg \
"

SRC_URI:append:lx2160ardb2bluebox = " \
    ${COMMONSRC_URI} \
    file://enablegfxhwls2.cfg \
"

# Note how our lxc.cfg comes *AFTER* containers.config to add to it
COMMONDELTA_KERNEL_DEFCONFIG = "blueboxconfig binfmt_misc.cfg enablepktgen.cfg iptables.cfg iso9660.cfg enableusbcan.cfg containers.config lxc.cfg bpfcgroup.cfg"
DELTA_KERNEL_DEFCONFIG:append:ls2 = " ls2blueboxconfig enablegfxhwls2.cfg ${COMMONDELTA_KERNEL_DEFCONFIG}"
#DELTA_KERNEL_DEFCONFIG:append:ls2 = " dpaa2debug.cfg"
DELTA_KERNEL_DEFCONFIG:append:ls2084abbmini = " gpio.cfg"
DELTA_KERNEL_DEFCONFIG:append:ls2084abluebox = " enablecortina.cfg"

DELTA_KERNEL_DEFCONFIG:append:lx2160abluebox3 = " enablegfxhwls2.cfg ${COMMONDELTA_KERNEL_DEFCONFIG}"
#DELTA_KERNEL_DEFCONFIG:append:lx2160abluebox3 = " dpaa2debug.cfg"

#DELTA_KERNEL_DEFCONFIG:append:ls1043ardb = " ${COMMONDELTA_KERNEL_DEFCONFIG}"
#DELTA_KERNEL_DEFCONFIG:append:ls1043ardb = " pci-vdev.cfg"

DELTA_KERNEL_DEFCONFIG:append = "${@bb.utils.contains('DISTRO_FEATURES', 'docker', ' docker.cfg', '', d)}"

#SRC_URI:append:ls2084abbmini = " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', ' file://0001-pcie-ls2-kernel-support-for-pcie-demos.patch', '', d)}"
#PCIE_DEMOS_LX2_PATCH ?= "0001-pcie-lx2-kernel-support-for-pcie-demos.patch"
#PCIE_DEMOS_LX2_PATCH:lx2160ahpcsom = "0001-pcie-lx2-hpcsom-kernel-support-for-pcie-demos.patch"
#SRC_URI:append:lx2160a = " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', ' file://${PCIE_DEMOS_LX2_PATCH}', '', d)}"
