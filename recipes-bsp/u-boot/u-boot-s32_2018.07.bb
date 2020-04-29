require recipes-bsp/u-boot/u-boot-s32.inc

SRC_URI_prepend = "git://source.codeaurora.org/external/autobsps32/u-boot;protocol=https;branch=alb/master "

SRCREV = "86e446bb87a57189af0d071877006c9018a6b1ea"

# Support for generating default environment
SRC_URI += " \
    file://0001-env-Add-Makefile-rule-to-generate-default-environment-2018.patch \
"
