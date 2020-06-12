require recipes-bsp/u-boot/u-boot-s32.inc

SRC_URI_prepend = "git://source.codeaurora.org/external/autobsps32/u-boot;protocol=https;branch=alb-2020.04 "

SRCREV = "410bbafb801c1380ac5e4281767207ea24a9c688"

# Support for generating default environment
SRC_URI += " \
    file://0001-env-Add-Makefile-rule-to-generate-default-environment-2019.04.patch \
"
