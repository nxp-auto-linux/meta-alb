SRCREV = "acfe4402df0a226d2036041ceef84bbcd775554a"

FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

COMMONSRC_URI = " \
	file://0001-kernel-Added-BB-Mini-device-tree.patch \
	file://0001-gpio-workaround-for-gpio-interrupts.patch \
\
	file://0001-cache-export-function-__dma_flush_area.patch \
\
    file://blueboxconfig \
    file://enablepktgen.cfg \
    file://iptables.cfg \
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

# add sources for virtual ethernet over PCIe
SRC_URI_append_ls2084abbmini += " \
    git://source.codeaurora.org/external/autobsps32/vnet;protocol=https;branch=alb/master;name=vnet;destsuffix=git/drivers/net/vnet \
    file://0001-vnet-Add-initial-support-to-build-driver-in-kernel.patch \
    file://vnet_ls2.cfg \
    file://0001-vnet-remove-iommu-map-for-pcie-in-dts.patch \
"
SRCREV_vnet = "a7d9c2ccb0c6bb03d652dce5721f8aa416220ac7"

# Optional to simplify Ethernet debug
#SRC_URI_append_ls2 += " \
#    file://dpaa2debugfs.cfg \
#"

SRC_URI_append_t4bluebox += " \
    ${COMMONSRC_URI} \
    file://t4blueboxconfig \
    file://enablegfxhwt4.cfg \
"

# pci vdev sources
SRC_URI_append_ls1043ardb += " \
    git://source.codeaurora.org/external/autobsps32/vnet;protocol=https;branch=pci-vdev;name=pci-vdev;destsuffix=git/drivers/pci/pci-vdev \
    file://0001-Add-support-for-building-NXP-VETH-module.patch \
    file://0001-LS1043A-Adjust-device-tree-ranges-for-PCIe.patch \
    file://pci-vdev.cfg \
"

SRCREV_pci-vdev = "26b0de749789c22779820051ea7992860abca19e"

# Note how our lxc.cfg comes *AFTER* containers.config to add to it
COMMONDELTA_KERNEL_DEFCONFIG = "enablepktgen.cfg iptables.cfg iso9660.cfg enableusbcan.cfg containers.config lxc.cfg"
DELTA_KERNEL_DEFCONFIG_append_ls2 = " ls2blueboxconfig dpaa2qdma.cfg dpaa2debugfs.cfg ${COMMONDELTA_KERNEL_DEFCONFIG}"
DELTA_KERNEL_DEFCONFIG_append_ls2084abbmini = " vnet_ls2.cfg"

#DELTA_KERNEL_DEFCONFIG_append_ls2 = " enablegfxhwls2.cfg"
DELTA_KERNEL_DEFCONFIG_append_t4bluebox = " t4blueboxconfig ${COMMONDELTA_KERNEL_DEFCONFIG}"
#DELTA_KERNEL_DEFCONFIG_append_t4bluebox = " enablegfxhwt4.cfg"
DELTA_KERNEL_DEFCONFIG_append_ls1043ardb = " ${COMMONDELTA_KERNEL_DEFCONFIG}"
DELTA_KERNEL_DEFCONFIG_append_ls1043ardb += " pci-vdev.cfg"
DELTA_KERNEL_DEFCONFIG_append += "${@bb.utils.contains('DISTRO_FEATURES', 'docker', 'docker.cfg', '', d)}"
