# Copyright (C) 2015 Freescale Semiconductor
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Freescale Package group for development tools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PACKAGE_ARCH = "${MACHINE_ARCH}"
inherit packagegroup

PACKAGES = "${PN}"

RDEPENDS_${PN} = " \
    bison \
    ccache \
    chrpath \
    diffstat \
    dtc \
    gettext-runtime \
    git \
    git-perltools \
    intltool \
    ncurses-dev \
    perl \
    perl-misc \
    perl-modules \
    perl-module-re \
    perl-pod \
    python \
    python-misc \
    python-modules \
    quilt \
    rpm \
    subversion \
    tcl \
    u-boot-mkimage \
"

RDEPENDS_${PN}_remove_qoriq-ppc = " \
    git \
    git-perltools \
    mdadm \
"

RDEPENDS_${PN}_append = " \
     expect \
     gmp-dev \
     libmpc-dev \
     mpfr-dev \
"
