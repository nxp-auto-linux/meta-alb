HSE_CONFIG = "hse_config.cfg"

DELTA_UBOOT_DEFCONFIG:append:s32cc = " ${HSE_CONFIG}"
SRC_URI:append:s32cc = " \
    file://build/${HSE_CONFIG} \
"
