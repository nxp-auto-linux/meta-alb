FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
	file://0001-u-boot-Enable-Vitesse-reset-delay-required-per-spec.patch \
	file://0002-u-boot-Correct-Vitesse-PHY-init-code.patch \
\
	file://0003-u-boot-Enable-single-rank-DDR-timing-properly-for-ol.patch \
\
	file://0007-u-boot-Fix-LS2-derivative-detection-with-AIOP-or-3rd.patch \
	file://0009-u-boot-Fix-personality-check-for-LS2r1-core-startup-.patch \
\
	file://0015-u-boot-Fix-MMU-setup-race-condition-on-SPL-boot.patch \
\
	file://0001-u-boot-Enabled-setexpr-for-LS2080ARDB-platform-U-Boo.patch \
\
	file://0001-u-boot-First-attempt-to-add-GIC-support-for-ARMv8.patch \
\
	file://0001-u-boot-SPL-NAND-support-should-only-be-active-if-NAN.patch \
"


