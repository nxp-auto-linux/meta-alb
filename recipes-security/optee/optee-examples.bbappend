PV = "3.18.0"

SRCREV = "f301ee9df2129c0db683e726c91dc2cefe4cdb65"

EXTRA_OEMAKE += " \
                LIBGCC_LOCATE_CFLAGS=--sysroot=${STAGING_DIR_HOST} \
                "

INSANE_SKIP_${PN} += "ldflags"

DEPENDS += "python3-cryptography-native"
