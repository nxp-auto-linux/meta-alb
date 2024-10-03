PV = "5.15.158"

require recipes-kernel/linux/linux-s32.inc

SRCREV = "edec6e1363dd9a0b17cab5552658c37259ed0bb1"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PV_MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI:append:s32cc = "${@ ' file://build/quickboot-generic_${PV_MAJ_VER}.cfg' if d.getVar('QUICK_BOOT_CONFIG', True) else ''}"
DELTA_KERNEL_DEFCONFIG:append:s32cc = "${@ ' quickboot-generic_${PV_MAJ_VER}.cfg' if d.getVar('QUICK_BOOT_CONFIG', True) else ''}"

# Static enablement of eMMC HS400 mode in case Verified-Boot
# and Quick-Boot features are both enabled.
VERIFIED_QUICK_BOOT = "${@bb.utils.contains('DISTRO_FEATURES', 'verifiedboot', ' file://build/0001-dts-Enable-eMMC-HS400-mode_${PV_MAJ_VER}.patch', '', d)}"
SRC_URI:append:s32cc = "${@ '${VERIFIED_QUICK_BOOT}' if d.getVar('QUICK_BOOT_CONFIG', True) else ''}"

# Enable eMMC CQE support
SRC_URI:append:s32g = " \
    ${@bb.utils.contains('EMMC_FEATURES', 'cqe', 'file://build/0002-sdhci-esdhc-imx-Enable-CQE-support-${PV_MAJ_VER}.patch', '', d)} \
"
