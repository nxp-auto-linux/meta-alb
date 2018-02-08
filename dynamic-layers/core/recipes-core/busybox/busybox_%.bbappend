FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://externaldhcp.cfg \
    file://enabledhcpcopts.patch \
"


