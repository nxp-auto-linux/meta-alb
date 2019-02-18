FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}-legacy:"

# These patches are relative to SDK-V2.0-1703
SRC_URI += "\
	file://0001-u-boot-Enable-Vitesse-reset-delay-required-per-spec.patch \
	file://0002-u-boot-Correct-Vitesse-PHY-init-code.patch \
	file://0003-u-boot-Enable-single-rank-DDR-timing-properly-for-ol.patch \
	file://0004-u-boot-Add-config-info-for-thermal-based-early-fan-c.patch \
	file://0005-u-boot-Enabled-early-I2C-board-specific-init-before-.patch \
	file://0006-u-boot-Early-fan-control-on-LS2-RDBs-based-on-sensor.patch \
	file://0007-u-boot-Fix-LS2-derivative-detection-with-AIOP-or-3rd.patch \
	file://0009-u-boot-Fix-personality-check-for-LS2r1-core-startup-.patch \
	file://0014-u-boot-Major-rework-of-the-VID-support.patch \
	file://0001-u-boot-qoriq-Bad-defined-check-caused-the-LTC3882-to.patch \
	file://0001-u-boot-qoriq-VID-support-code-for-LTC-was-incorrect.patch \
	file://0001-u-boot-qoriq-VID-code-blocked-interrupts.patch \
	file://0002-u-boot-qoriq-Reworked-VID-parameters-based-on-IR-LTC.patch \
	file://0015-u-boot-Fix-MMU-setup-race-condition-on-SPL-boot.patch \
\
	file://0001-u-boot-First-attempt-to-add-GIC-support-for-ARMv8-ne.patch \
	file://0001-u-boot-Enable-GIC-support-to-permit-PCIe-EP-use-on-t.patch \
	file://0001-u-boot-Enabled-setexpr-for-LS2080ARDB-platform-U-Boo.patch \
\
	file://0001-u-boot-Fix-LS2088A-style-VF-setup-on-EPs.patch \
"

#
#
#
###	file://0001-u-boot-SPL-NAND-support-should-only-be-active-if-NAN.patch \
#
###	file://0001-u-boot-Fixup-random-MAC-addresses-in-DPL-based-on-U-.patch \
#
#
