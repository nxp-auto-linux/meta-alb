PV = "5.15.179"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "be2b4ba5591b690e6036c1585a960295d1972bf0"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

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
