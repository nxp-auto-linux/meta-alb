FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://externaldhcp.cfg \
    file://enabledhcpcopts.patch \
"

# disable FTPD - as it's provided by inetutils
do_configure_append() {
    sed -i "/CONFIG_FTPD/c\# CONFIG_FTPD is not set" .config
}

