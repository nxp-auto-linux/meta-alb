FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
	file://0001-u-boot-Basic-set-of-BB-Mini-configuration-added.patch \
	file://0001-u-boot-Adapted-the-ls2084abbmini-config-to-the-LSDK-.patch \
	file://0001-u-boot-qoriq-Missing-USB-nodes-in-the-dts-for-the-ls.patch \
	file://0002-u-boot-qoriq-Special-config-for-LS2080ARDB-with-LS20.patch \
	file://0001-Resolve-multiple-undefined-redefined-build-errors.patch \
    file://0001-iommu-Fix-iommu-map-entry-in-fsl-mc-node-from-dts.patch \
	file://0001-env-Fix-environment-location-flash-mmc.patch \
	file://0001-ls2084ardb_defconfig-Config-was-broken-and-U-Boot-di.patch \
"

SRCREV_ls1043ardb = "9f7df1b406ff11409021cd2112beedd6b57bb600"

