FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI_remove="http://linuxcontainers.org/downloads/${BPN}-${PV}.tar.gz "
SRC_URI_prepend="http://linuxcontainers.org/downloads/${BPN}/${BPN}-${PV}.tar.gz "

SRC_URI += "file://0001-Update-busybox-template-to-handle-nosuid-suid-flavor.patch"
