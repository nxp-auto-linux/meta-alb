PV = "3.9.0"

SRCREV = "f461e1d47fcc82eaa67508a3d796c11b7d26656e"

DEPENDS += "python3-pycryptodomex-native"

EXTRA_OEMAKE += " \
                WITH_OPENSSL=n \
                "