# Copyright 2023 NXP
#
# TrustZone Key Provisioning Demo Installer

DEPENDS += "optee-client openssl-native"

EXTRA_OEMAKE += " \
		TEEC_EXPORT=${STAGING_DIR_HOST}${prefix} \
                HOST_CC='${BUILD_CC}' \
		HOST_CFLAGS='${BUILD_CFLAGS}' \
		HOST_LDFLAGS='${BUILD_LDFLAGS}' \
		HOST_OPENSSL_LIB=${STAGING_LIBDIR_NATIVE} \
		HOST_OPENSSL_INC=${STAGING_INCDIR_NATIVE} \
		"
do_generate_keys() {
	oe_runmake -C ${S}/examples trustzone-key-provision PKCS11HSE_DIR="${S}"
	${S}/examples/trustzone-key-provision/host/aes-gcm-key-encryption ${B}/kek.bin ${B}/enckey.bin
}

addtask generate_keys after do_compile before do_install

do_install:append() {
	install -d ${D}${datadir}

	oe_runmake -C "${S}/examples" install_trustzone-key-provision EXAMPLES_INSTALLDIR="${D}${bindir}"
	install ${B}/kek.bin ${D}${datadir}
	install ${B}/enckey.bin ${D}${datadir}
}

FILES:${PN} += "${datadir}/kek.bin ${datadir}/enckey.bin"
