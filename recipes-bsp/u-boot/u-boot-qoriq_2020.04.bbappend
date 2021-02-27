FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

EXTRA_OEMAKE += 'STAGING_INCDIR=${STAGING_INCDIR_NATIVE} STAGING_LIBDIR=${STAGING_LIBDIR_NATIVE}'

SRC_URI += "\
	file://0001-u-boot-Fixing-I2C-support-in-non-TFA-configurations.patch \
	file://0002-u-boot-Basic-set-of-BB-Mini-configuration-added.patch \
	file://0001-ls2084abbmini-Change-StreamID-allocation.patch \
\
	file://0001-u-boot-TFA-boot-config-for-ls2084abbmini.patch \
	file://0001-u-boot-TFA-config-for-ls2080abluebox-ls2084abluebox.patch \
"

#SRCREV_ls1043ardb = "9f7df1b406ff11409021cd2112beedd6b57bb600"

