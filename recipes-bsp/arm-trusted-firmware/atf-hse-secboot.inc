# Copyright 2023 NXP

DEPENDS += "arm-trusted-firmware-tools-native"

KEYS_DIR = "${WORKDIR}/keys"
RSA_PRIV_FIP = "${KEYS_DIR}/fip_rsa2048_private.pem"
RSA_PUB_FIP = "${KEYS_DIR}/fip_rsa2048_public.pem"
RSA_PUB_FIP_FILE = "fip_rsa2048_public.pem"
INSTALL_PATH = "/usr/keys"

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

do_install:append() {
       install -d ${D}${INSTALL_PATH}
       install -m 0666 ${RSA_PUB_FIP} ${D}${INSTALL_PATH}
}

FILES:${PN}-secboot = " ${INSTALL_PATH}/${RSA_PUB_FIP_FILE} "
PROVIDES += "${PN}-secboot"
PACKAGES += "${PN}-secboot"
