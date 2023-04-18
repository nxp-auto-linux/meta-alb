FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

EXTRA_OEMAKE += 'STAGING_INCDIR=${STAGING_INCDIR_NATIVE} STAGING_LIBDIR=${STAGING_LIBDIR_NATIVE}'

SRC_URI += "\
	file://0001-u-boot-Basic-set-of-BB-Mini-configuration-added.patch \
	file://0001-u-boot-qoriq-Support-of-LS2-RDB-as-BlueBox-1.patch \
	file://0001-u-boot-qoriq-Support-of-LX2160A-BlueBox3.patch \
	file://0001-u-boot-qoriq-Support-of-LX2-RDB-with-BlueBox-image.patch \
"
