require recipes-bsp/u-boot/u-boot-nxp.inc
require recipes-bsp/u-boot/u-boot-src-${PV}.inc

LIC_FILES_CHKSUM += " \
    file://Licenses/lgpl-2.0.txt;md5=4cf66a4984120007c9881cc871cf49db \
"

# Support for generating default environment
SRC_URI += " \
    file://0001-env-Add-Makefile-rule-to-generate-default-environment-${PV}.patch \
"
