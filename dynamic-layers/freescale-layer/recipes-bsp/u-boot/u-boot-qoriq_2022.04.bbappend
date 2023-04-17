FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
	file://0001-u-boot-Enabled-setexpr-for-LS2080ARDB-platform-U-Boo.patch \
	file://0002-u-boot-SPL-NAND-support-should-only-be-active-for-NA.patch \
	file://0003-u-boot-Fix-LS2-derivative-detection.patch \
	file://0004-u-boot-Enable-single-rank-DDR-timing-for-older-T4-RD.patch \
	file://0005-u-boot-Correct-Vitesse-PHY-init-code.patch \
\
	file://0001-u-boot-First-attempt-to-add-GIC-support-for-ARMv8.patch \
\
	file://0001-u-boot-qoriq-LS208xARDB-Cortina-check-was-broken.patch \
\
	file://LFU-459.patch \
\
	file://0001-u-boot-qoriq-Enable-VID-for-LTC7132.patch \
	file://0002-u-boot-qoriq-Enable-SerDes-1-0x1F-as-accepted-config.patch \
"
