require recipes-bsp/u-boot/u-boot-nxp.inc
require recipes-bsp/u-boot/u-boot-src.inc

# Support for generating default environment
SRC_URI += " \
    file://0001-env-Add-Makefile-rule-to-generate-default-environment-2019.04.patch \
"
