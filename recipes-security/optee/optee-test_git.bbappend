PV = "3.11.0"

SRCREV = "159e295d5cc3ad2275ab15fe544620f6604d4ba4"

DEPENDS += "python3-pycryptodomex-native"

EXTRA_OEMAKE += " \
                WITH_OPENSSL=n \
                LIBGCC_LOCATE_CFLAGS=--sysroot=${STAGING_DIR_HOST} \
                "