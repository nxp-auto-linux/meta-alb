PV = "6.6.99"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "1ea3efb7d1409fd997dff589f32309b8e7f33e10"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

# Kernel 6.6 specific support for VirtIO with Xen
DELTA_KERNEL_DEFCONFIG:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen virtio', 'xen_virtio_${PV_MAJ_VER}.cfg', '', d)} \
"
SRC_URI += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen virtio', 'file://xen_virtio_${PV_MAJ_VER}.cfg', '', d)} \
"

SRC_URI:append:s32cc = "${@ ' file://quickboot-generic_${PV_MAJ_VER}.cfg' if d.getVar('QUICK_BOOT_CONFIG', True) else ''}"
DELTA_KERNEL_DEFCONFIG:append:s32cc = "${@ ' quickboot-generic_${PV_MAJ_VER}.cfg' if d.getVar('QUICK_BOOT_CONFIG', True) else ''}"

# Static enablement of eMMC HS400 mode in case Verified-Boot
# and Quick-Boot features are both enabled.
VERIFIED_QUICK_BOOT = "${@bb.utils.contains('DISTRO_FEATURES', 'verifiedboot', ' file://0001-dts-Enable-eMMC-HS400-mode_${PV_MAJ_VER}.patch', '', d)}"
SRC_URI:append:s32cc = "${@ '${VERIFIED_QUICK_BOOT}' if d.getVar('QUICK_BOOT_CONFIG', True) else ''}"

# Enable eMMC CQE support
SRC_URI:append:s32g = " \
    ${@bb.utils.contains('EMMC_FEATURES', 'cqe', 'file://0002-sdhci-esdhc-imx-Enable-CQE-support-${PV_MAJ_VER}.patch', '', d)} \
"

# Add quirks for S32R478A-EVB Ethernet enablement
SRC_URI:append:s32r478 = " file://0001-net-phy-nxp-c45-tja11xx-Force-disable-SGMII-Autoneg.patch"
