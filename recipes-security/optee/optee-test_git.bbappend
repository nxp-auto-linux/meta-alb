PV = "3.18.0"

SRCREV = "da5282a011b40621a2cf7a296c11a35c833ed91b"

DEPENDS += "python3-cryptography-native"

EXTRA_OEMAKE += " \
                WITH_OPENSSL=n \
                LIBGCC_LOCATE_CFLAGS=--sysroot=${STAGING_DIR_HOST} \
                "
do_compile_append () {
        oe_runmake test_plugin
}

do_install_append () {
        mkdir -p ${D}${libdir}/tee-supplicant/plugins
        install -D -p -m0444 ${S}/out/supp_plugin/*.plugin ${D}${libdir}/tee-supplicant/plugins/
}

FILES_${PN} += "\
                ${libdir}/tee-supplicant/plugins/ \
               "
