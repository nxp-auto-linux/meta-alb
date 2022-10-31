FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:${THISDIR}/files:"


COMMONSRC_URI = " \
	file://0001-kernel-Added-BB-Mini-device-tree-5.4.patch \
	file://0001-gpio-workaround-for-gpio-interrupts.patch \
\
    file://blueboxconfig \
    file://enablepktgen.cfg \
    file://iptables_5.4.cfg \
    file://iso9660.cfg \
    file://enableusbcan.cfg \
    file://enableusbftdi.cfg \
    file://containers.config \
    file://docker.cfg \
    file://lxc.cfg \
"

SRC_URI_append_ls2 += " \
    ${COMMONSRC_URI} \
    file://ls2blueboxconfig \
    file://enablegfxhwls2.cfg \
    file://dpaa2qdma.cfg \
"

SRC_URI_append_ls2084abbmini += " \
    file://gpio.cfg \
"

# Optional to simplify Ethernet debug
#SRC_URI_append_ls2 += " \
#    file://dpaa2debugfs.cfg \
#"

# pci vdev sources
SRC_URI_append_ls1043ardb += " \
    https://github.com/nxp-auto-linux/vnet;protocol=https;branch=pci-vdev;name=pci-vdev;destsuffix=git/drivers/pci/pci-vdev \
    file://0001-Add-support-for-building-NXP-VETH-module.patch \
    file://0001-LS1043A-Adjust-device-tree-ranges-for-PCIe.patch \
    file://pci-vdev.cfg \
"

SRCREV_pci-vdev = "3646332fa76ef1623b36b6fe36e43391029c4603"

# Note how our lxc.cfg comes *AFTER* containers.config to add to it
COMMONDELTA_KERNEL_DEFCONFIG = "enablepktgen.cfg iptables_5.4.cfg iso9660.cfg enableusbcan.cfg containers.config lxc.cfg"
DELTA_KERNEL_DEFCONFIG_append_ls2 = " ls2blueboxconfig dpaa2qdma.cfg dpaa2debugfs.cfg ${COMMONDELTA_KERNEL_DEFCONFIG}"
DELTA_KERNEL_DEFCONFIG_append_ls2084abbmini = " gpio.cfg"

#DELTA_KERNEL_DEFCONFIG_append_ls2 = " enablegfxhwls2.cfg"

DELTA_KERNEL_DEFCONFIG_append_ls1043ardb = " ${COMMONDELTA_KERNEL_DEFCONFIG}"
DELTA_KERNEL_DEFCONFIG_append_ls1043ardb += " pci-vdev.cfg"
DELTA_KERNEL_DEFCONFIG_append += "${@bb.utils.contains('DISTRO_FEATURES', 'docker', 'docker.cfg', '', d)}"

SRC_URI_append_ls2084abbmini += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', 'file://0001-pcie-ls2-kernel-support-for-pcie-demos.patch', '', d)}"

PCIE_DEMOS_LX2_PATCH ?= "0001-pcie-lx2-kernel-support-for-pcie-demos.patch"
PCIE_DEMOS_LX2_PATCH_lx2160ahpcsom = "0001-pcie-lx2-hpcsom-kernel-support-for-pcie-demos.patch"
SRC_URI_append_lx2160a += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', 'file://${PCIE_DEMOS_LX2_PATCH}', '', d)}"

require gcc75compat.inc
