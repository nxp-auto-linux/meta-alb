require recipes-bsp/u-boot/u-boot-s32.inc

SRC_URI_prepend = "git://source.codeaurora.org/external/autobsps32/u-boot;protocol=https;branch=alb/master "

SRCREV = "bdd597e96364b37e60f989c859474142cf6a4d90"

# Support for generating default environment
SRC_URI += " \
    file://0001-env-Add-Makefile-rule-to-generate-default-environment-2018.patch \
"
