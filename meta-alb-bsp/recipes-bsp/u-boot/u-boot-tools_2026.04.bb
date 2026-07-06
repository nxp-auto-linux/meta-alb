require recipes-bsp/u-boot/u-boot-tools-nxp.inc
require recipes-bsp/u-boot/u-boot-src-${PV}.inc

DEPENDS += "libyaml-native"

LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"
