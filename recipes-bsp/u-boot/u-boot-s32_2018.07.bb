require recipes-bsp/u-boot/u-boot-s32.inc

SRC_URI_prepend = "git://source.codeaurora.org/external/autobsps32/u-boot;protocol=https;branch=alb/master "

SRCREV = "0d3223122ac0149b7ea5dd2be334835620de500a"

# Support for generating default environment
SRC_URI += " \
    file://0001-env-Add-Makefile-rule-to-generate-default-environment-2018.patch \
"
