# Provides common settings for S32CC boards, emulation and simulation models
require conf/machine/include/s32-base.inc
require conf/machine/include/arm/armv8a/tune-cortexa53.inc

SOC_FAMILY:append = ":s32cc"

DEFAULTTUNE:s32cc ?= "cortexa53-crypto"

# we do not want to have getty running on tty1 as we run
# auto-serial-console there USE_VT = "0"
SERIAL_CONSOLES ?= "${@bb.utils.contains('QUICK_BOOT_CONFIG', 'uart2mbit', '2000000;ttyLF0', '115200;ttyLF0', d)}"

SERIAL_CONSOLES_CHECK ?= "ttyLF0:ttyLF0 ttyLF1:ttyLF1 ttyLF2:ttyLF2 " \
                         "ttyLF3:ttyLF3 ttyLF4:ttyLF4 ttyLF5:ttyLF5 ttyLF6:ttyLF6"
