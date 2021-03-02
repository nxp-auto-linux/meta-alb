FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

EXTRA_OEMAKE += 'STAGING_INCDIR=${STAGING_INCDIR_NATIVE} STAGING_LIBDIR=${STAGING_LIBDIR_NATIVE}'

SRC_URI += "\
	file://0001-u-boot-Fixing-I2C-support-in-non-TFA-configurations.patch \
	file://0002-u-boot-Basic-set-of-BB-Mini-configuration-added.patch \
	file://0001-ls2084abbmini-Change-StreamID-allocation.patch \
"

#	file://0002-u-boot-qoriq-Special-config-for-LS2080ARDB-with-LS20.patch \
#	file://0001-ls2084ardb_defconfig-Config-was-broken-and-U-Boot-di.patch \
#

#SRCREV_ls1043ardb = "9f7df1b406ff11409021cd2112beedd6b57bb600"

