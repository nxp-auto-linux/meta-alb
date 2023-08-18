# Copyright 2023 NXP

DEPENDS += "arm-trusted-firmware-tools-native"

# Secure boot works only on sdcard
BOOT_TYPE = "sdcard"

KEYS_DIR = "${WORKDIR}/keys"
RSA_PRIV_FIP = "${KEYS_DIR}/fip_rsa2048_private.pem"
RSA_PUB_FIP = "${KEYS_DIR}/fip_rsa2048_public.pem"

do_generate_keys () {
	mkdir -p ${KEYS_DIR}
	openssl genrsa -out ${RSA_PRIV_FIP} 2048
	openssl rsa -in ${RSA_PRIV_FIP} -pubout -out ${RSA_PUB_FIP}
}

do_generate_keys[depends] += "openssl-native:do_populate_sysroot"
addtask do_generate_keys before do_compile after do_unpack

do_deploy:append() {
	cp -vf "${RSA_PRIV_FIP}" ${DEPLOYDIR}
	cp -vf "${RSA_PUB_FIP}" ${DEPLOYDIR}
}
