FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI_remove = "http://linuxcontainers.org/downloads/${BPN}-${PV}.tar.gz"

SRC_URI += "http://linuxcontainers.org/downloads/${BPN}/${BPN}-${PV}.tar.gz \
			\
			file://0001-Update-busybox-template-to-handle-nosuid-suid-flavor.patch \
			file://0002-lxc-network-Add-corner-case-on-ifup.patch"
