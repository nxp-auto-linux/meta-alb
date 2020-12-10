FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

PV = "5.4"
SRC_URI_append_ls2 += "\
	file://0001-kernel-LS2-RDB-device-tree-was-not-quite-correct.patch \
	file://0001-kernel-Added-phy-ioctl-support-to-the-DPAA2-dpmac-dr.patch \
	file://0001-linux-qoriq-arm64-kernel-now-conserves-some-power-wh-5.4.patch \
	file://0001-dpaa2-eth-Keep-the-correspondence-between-dpni-id-4.19.patch \
"

# We don't know at this stage if we want to keep or drop these. They
# are likely to be obsoleted by the next kernel pick.
DUMMYTBD="\
    file://0001-staging-fsl-mc-dpio-Reimplement-service-selection.patch \
    file://0002-staging-fsl-mc-dpio-Update-DPIO-service-API-for-eth.patch \
    file://0003-staging-fsl-dpaa2-Call-DPIO-service-API-with-CPU-aff.patch \
"

# The following pcie patches regarding LS-EP functionality are temporary
# disabled due to linux kernel qoriq version update to 4.14.
DUMMYTBD += "\
    file://0001-kernel-Complete-rework-of-the-LS-EP-driver.patch \
    file://0001-pci-layerscape-Fixed-up-register-and-bit-naming-conv.patch \
    file://0002-pci-layerscape.c-LUT-endianess-and-presence-was-not-.patch \
"

SRC_URI_append_ls1043ardb = " file://build/enableds1307.cfg"
DELTA_KERNEL_DEFCONFIG_append_ls1043ardb = " enableds1307.cfg"
