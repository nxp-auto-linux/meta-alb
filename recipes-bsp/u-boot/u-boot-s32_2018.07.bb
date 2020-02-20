require recipes-bsp/u-boot/u-boot-s32.inc

SRC_URI_prepend = "git://source.codeaurora.org/external/autobsps32/u-boot;protocol=https;branch=alb/master "

SRCREV = "a827f1299f428dac01f38b9729abbb2d91082ec6"

# Support for generating default environment
SRC_URI += " \
    file://0001-env-Add-Makefile-rule-to-generate-default-environment-2018.patch \
"
