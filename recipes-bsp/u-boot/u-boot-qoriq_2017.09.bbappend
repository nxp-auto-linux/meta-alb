FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
	file://0001-u-boot-Basic-set-of-BB-Mini-configuration-added.patch \
	file://0001-u-boot-Adapted-the-ls2084abbmini-config-to-the-LSDK-.patch \
	file://0001-u-boot-qoriq-Bad-defined-check-caused-the-LTC3882-to.patch \
	file://0001-iu-boot-qoriq-VID-support-code-for-LTC-was-incorrect.patch \
"
#	file://0001-u-boot-Disable-PSCI_RESET-for-BB-Mini.patch \
#	file://0001-u-boot-Special-config-for-LS2080ARDB-with-LS2084A-si.patch \
#
