# Copyright (C) 2015 Freescale Semiconductor
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Freescale Package group for extended networking tools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PACKAGE_ARCH = "${MACHINE_ARCH}"
inherit packagegroup

PACKAGES = "${PN}"

FSL_WEBSERVER ?= ""
RDEPENDS:${PN} = " \
    bind \
    curl \
    dhcpcd \
    libnfnetlink \
    linuxptp \
    ntpdate \
    ppp \
    ppp-dialin \
    resolvconf \
    samba \
    wget \
    xinetd \
    ${FSL_WEBSERVER} \
"
