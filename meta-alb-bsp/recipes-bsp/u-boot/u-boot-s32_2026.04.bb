require recipes-bsp/u-boot/u-boot-nxp.inc
require recipes-bsp/u-boot/u-boot-src-${PV}.inc

# mkeficapsule is built as a host tool during the firmware compile and needs
# gnutls headers in the native sysroot.
DEPENDS += "gnutls-native"

LIC_FILES_CHKSUM += " \
    file://Licenses/lgpl-2.0.txt;md5=4cf66a4984120007c9881cc871cf49db \
"

# Support for generating default environment (single patch shared across
# releases: the rule depends on the `env` dir, not a version-specific object)
SRC_URI += " \
    file://0001-env-Add-Makefile-rule-to-generate-default-environment.patch \
"
