SUMMARY = "Xen hypervisor tools written in python 2"
DESCRIPTION = "Unported utility scripts for the Xen hypervisor"
HOMEPAGE = "http://xen.org"
LICENSE = "GPLv2"
SECTION = "console/tools"

SRCREV ?= "a5fcafbfbee55261853fba07149c1c795f2baf58"

# The same restriction as xen-tools.inc, because of the dependency on xen-tools-xentrace from xen-tools-xentrace-format
COMPATIBLE_HOST = 'i686-.*-linux|(x86_64.*).*-linux|aarch64.*-linux|arm-.*-linux-gnueabi'

XEN_REL ?= "4.12"
XEN_BRANCH ?= "stable-4.12"

SRC_URI = "git://xenbits.xen.org/xen.git;branch=${XEN_BRANCH}"

LIC_FILES_CHKSUM ?= "file://COPYING;md5=bbb4b1bdc2c3b6743da3c39d03249095"

PV = "${XEN_REL}+git${SRCPV}"

S = "${WORKDIR}/git"

# Packages in this recipe do not use ${PN} to allow for simpler
# movement of the package back into the xen-tools recipe if/when
# the scripts are ported to python 3.

RDEPENDS_xen-tools-xencov-split ="python"
RDEPENDS_xen-tools-xencons = "python"
RDEPENDS_xen-tools-xenpvnetboot = "python"
RDEPENDS_xen-tools-xentrace-format = "python"

RRECOMMENDS_xen-tools-xencov-trace = "xen-tools-xencov"
RRECOMMENDS_xen-tools-xentrace-format = "xen-tools-xentrace"

PACKAGES = " \
    xen-tools-xencons \
    xen-tools-xencov-split \
    xen-tools-xenpvnetboot \
    xen-tools-xentrace-format \
    "

FILES_xen-tools-xencons = " \
    ${bindir}/xencons \
    "

FILES_xen-tools-xencov-split = " \
    ${bindir}/xencov_split \
    "

FILES_xen-tools-xenpvnetboot = " \
    ${libdir}/xen/bin/xenpvnetboot \
    "

FILES_xen-tools-xentrace-format = " \
    ${bindir}/xentrace_format \
    "

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/tools/xentrace/xentrace_format \
                    ${D}${bindir}/xentrace_format
    install -m 0755 ${S}/tools/misc/xencons ${D}${bindir}/xencons
    install -m 0755 ${S}/tools/misc/xencov_split ${D}${bindir}/xencov_split

    install -d ${D}${libdir}/xen/bin
    install -m 0755 ${S}/tools/misc/xenpvnetboot \
                    ${D}${libdir}/xen/bin/xenpvnetboot
}
