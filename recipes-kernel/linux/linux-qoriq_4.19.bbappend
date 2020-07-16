FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

COMMONSRC_URI = " \
	file://0001-kernel-Added-BB-Mini-device-tree-4.19.patch \
	file://0001-gpio-workaround-for-gpio-interrupts.patch \
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
    file://binfmt_misc.cfg \
"
# TODO zeus: do not apply to kernel 4.19:
#   file://0001-cache-export-function-__dma_flush_area.patch

SRC_URI_append_ls2 += " \
    ${COMMONSRC_URI} \
    file://ls2blueboxconfig \
    file://enablegfxhwls2.cfg \
    file://dpaa2qdma.cfg \
"

# add sources for virtual ethernet over PCIe
SRC_URI_append_ls2084abbmini += " \
    git://source.codeaurora.org/external/autobsps32/vnet;protocol=https;branch=alb/master;name=vnet;destsuffix=git/drivers/net/vnet \
    file://0001-vnet-Add-initial-support-to-build-driver-in-kernel-4.19.patch \
    file://vnet_ls2.cfg \
    file://0001-vnet-remove-iommu-map-for-pcie-in-dts-4.19.patch \
    file://gpio.cfg \
"
SRCREV_vnet = "7d3c52dc3c6564e92bbbc0d0b6aa11e73174fed0"

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

SRCREV_pci-vdev = "3646332fa76ef1623b36b6fe36e43391029c4603"

# Note how our lxc.cfg comes *AFTER* containers.config to add to it
COMMONDELTA_KERNEL_DEFCONFIG = "enablepktgen.cfg iptables.cfg iso9660.cfg enableusbcan.cfg containers.config lxc.cfgi binfmt_misc.cfg"
DELTA_KERNEL_DEFCONFIG_append_ls2 = " ls2blueboxconfig dpaa2qdma.cfg dpaa2debugfs.cfg ${COMMONDELTA_KERNEL_DEFCONFIG}"
DELTA_KERNEL_DEFCONFIG_append_ls2084abbmini = " vnet_ls2.cfg gpio.cfg"

#DELTA_KERNEL_DEFCONFIG_append_ls2 = " enablegfxhwls2.cfg"
DELTA_KERNEL_DEFCONFIG_append_t4bluebox = " t4blueboxconfig ${COMMONDELTA_KERNEL_DEFCONFIG}"
#DELTA_KERNEL_DEFCONFIG_append_t4bluebox = " enablegfxhwt4.cfg"
DELTA_KERNEL_DEFCONFIG_append_ls1043ardb = " ${COMMONDELTA_KERNEL_DEFCONFIG}"
DELTA_KERNEL_DEFCONFIG_append_ls1043ardb += " pci-vdev.cfg"
DELTA_KERNEL_DEFCONFIG_append += "${@bb.utils.contains('DISTRO_FEATURES', 'docker', 'docker.cfg', '', d)}"

SRC_URI_append_ls2084abbmini += " ${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', 'file://0001-pcie-ls2-kernel-support-for-pcie-demos-icc-and-user-spa.patch', '', d)}"
