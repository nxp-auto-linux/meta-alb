SUMMARY = "Libcli is a shared library for a Cisco-like command-line interface"
DESCRIPTION = "Libcli provides a shared library for including a Cisco-like command-line interface into other software."
HOMEPAGE = "https://dparrish.com/pages/libcli"
SECTION = "console/devel"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=cb8aedd3bced19bd8026d96a8b6876d7"

DEPENDS = "virtual/crypt"

SRCREV = "4ef06297a2b056a5841de3e3bfa6cc43ac46e7b3"
SRC_URI = "git://github.com/dparrish/libcli.git;protocol=https;branch=stable \
    file://make-build-tools-overridable.patch \
"

S = "${WORKDIR}/git"

do_install() {
    oe_runmake PREFIX=${D}/${prefix} install
}

FILES:${PN} = "${libdir}"
