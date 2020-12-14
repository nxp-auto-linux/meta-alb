PV = "3.11.0"

SRCREV = "9a7dc598591990349d88b4dba3a37aadd6851295"

INSANE_SKIP_${PN} += "ldflags"

inherit python3native

DEPENDS += "python3-pycryptodomex-native"
