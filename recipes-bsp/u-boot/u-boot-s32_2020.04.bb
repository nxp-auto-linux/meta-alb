require recipes-bsp/u-boot/u-boot-nxp.inc

URL ?= "git://source.codeaurora.org/external/autobsps32/u-boot;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI_prepend = "${URL};branch=${BRANCH}"

SRCREV = "3ce09ace883cb76c6801ef666911b9f20206a7f4"

# Support for generating default environment
SRC_URI += " \
    file://0001-env-Add-Makefile-rule-to-generate-default-environment-2019.04.patch \
"
