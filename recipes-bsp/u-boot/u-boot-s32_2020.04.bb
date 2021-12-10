require recipes-bsp/u-boot/u-boot-nxp.inc

URL ?= "git://source.codeaurora.org/external/autobsps32/u-boot;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI_prepend = "${URL};branch=${BRANCH}"

SRCREV = "24a87fe460af8eba7128521d143049a101b4c303"

# Support for generating default environment
SRC_URI += " \
    file://0001-env-Add-Makefile-rule-to-generate-default-environment-2019.04.patch \
"
