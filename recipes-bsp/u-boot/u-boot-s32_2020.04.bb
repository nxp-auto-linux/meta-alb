require recipes-bsp/u-boot/u-boot-s32.inc

SRC_URI_prepend = "git://source.codeaurora.org/external/autobsps32/u-boot;protocol=https;branch=alb-2020.04 "

SRCREV = "0d6c42cc67e159666bf56faf47d4ce3c0ee65a5f"

# Support for generating default environment
SRC_URI += " \
    file://0001-env-Add-Makefile-rule-to-generate-default-environment-2019.04.patch \
"
