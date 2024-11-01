# Copyright 2023,2024 NXP

DEPENDS:append = " arm-trusted-firmware-tools-native"

SECBOOT_KEYS_DIR = "${WORKDIR}/secboot_keys"
SECBOOT_SCRIPT_FILE = "secboot_script.sh"
SECBOOT_SCRIPT = "${SECBOOT_KEYS_DIR}/${SECBOOT_SCRIPT_FILE}"
OP_TEE = "${@bb.utils.contains('DISTRO_FEATURES', 'optee', '1', '0', d)}"

BL2_SIGN_FILE = "bl2-signature.bin"
BL2_SIGN = "${TFA_BINARIES}/${BL2_SIGN_FILE}"

BL2_HANDLE ?= "0x010700"
BL31_HANDLE ?= "${BL2_HANDLE}"
BL32_HANDLE ?= "${BL2_HANDLE}"
BL33_HANDLE ?= "${BL2_HANDLE}"

RSA_PRIV_BL2_FILE ?= "bl2_rsa2048_private.pem"

RSA_PUB_BL2_FILE = "bl2_rsa2048_public.pem"
RSA_PUB_BL31_FILE = "bl31_rsa2048_public.pem"
RSA_PUB_BL32_FILE = "bl32_rsa2048_public.pem"
RSA_PUB_BL33_FILE = "bl33_rsa2048_public.pem"

RSA_PRIV_BL2 ?= "${SECBOOT_KEYS_DIR}/${RSA_PRIV_BL2_FILE}"
RSA_PRIV_BL31 ?= "${RSA_PRIV_BL2}"
RSA_PRIV_BL32 ?= "${RSA_PRIV_BL2}"
RSA_PRIV_BL33 ?= "${RSA_PRIV_BL2}"

RSA_PRIV_KEYS = "\
                 ${RSA_PRIV_BL2} \
                 ${RSA_PRIV_BL31} \
                 ${RSA_PRIV_BL33} \
                "

RSA_PUB_BL2 = "${SECBOOT_KEYS_DIR}/${RSA_PUB_BL2_FILE}"
RSA_PUB_BL31 = "${SECBOOT_KEYS_DIR}/${RSA_PUB_BL31_FILE}"
RSA_PUB_BL32 = "${SECBOOT_KEYS_DIR}/${RSA_PUB_BL32_FILE}"
RSA_PUB_BL33 = "${SECBOOT_KEYS_DIR}/${RSA_PUB_BL33_FILE}"

RSA_PUB_KEYS = "\
                ${RSA_PUB_BL2} \
                ${RSA_PUB_BL31} \
                ${RSA_PUB_BL33} \
               "

INSTALL_PATH_SECBOOT_KEYS = "/etc/keys/secboot/"

SYSROOT_DIRS:append  = " \
                           /etc/keys/secboot/ \
                       "

do_generate_bl_public_keys () {
    openssl rsa -in ${RSA_PRIV_BL2} -pubout -out ${RSA_PUB_BL2}
    openssl rsa -in ${RSA_PRIV_BL31} -pubout -out ${RSA_PUB_BL31}
    if [ ${OP_TEE} -eq 1 ]; then
        openssl rsa -in ${RSA_PRIV_BL32} -pubout -out ${RSA_PUB_BL32}
    fi
    openssl rsa -in ${RSA_PRIV_BL33} -pubout -out ${RSA_PUB_BL33}
}

do_generate_secboot_script() {
	echo -n "hse-secboot -s -b \$1 -d \$2" > ${SECBOOT_SCRIPT}
	echo -n " --bl2_key ${INSTALL_PATH_SECBOOT_KEYS}${RSA_PUB_BL2_FILE}:${BL2_HANDLE}" >> ${SECBOOT_SCRIPT}
	echo -n " --bl31_key ${INSTALL_PATH_SECBOOT_KEYS}${RSA_PUB_BL31_FILE}:${BL31_HANDLE}" >> ${SECBOOT_SCRIPT}
	if [ ${OP_TEE} -eq 1 ]; then
		echo -n " --bl32_key ${INSTALL_PATH_SECBOOT_KEYS}${RSA_PUB_BL32_FILE}:${BL32_HANDLE}" >> ${SECBOOT_SCRIPT}
	fi
	echo -n " --bl33_key ${INSTALL_PATH_SECBOOT_KEYS}${RSA_PUB_BL33_FILE}:${BL33_HANDLE}" >> ${SECBOOT_SCRIPT}
	echo -n " --bl2_bin \$3" >> ${SECBOOT_SCRIPT}
	echo " --bl2_sign \$4" >> ${SECBOOT_SCRIPT}
}

do_generate_bl_public_keys[depends] += "openssl-native:do_populate_sysroot"
addtask do_generate_bl_public_keys after do_compile before do_deploy
addtask do_generate_secboot_script after do_generate_bl_public_keys before do_install

do_compile:prepend () {
	mkdir -p ${SECBOOT_KEYS_DIR}
}

do_deploy:append() {
	for suffix in ${BOOT_TYPE}
	do
		cp -vf ${BL2_SIGN}-${suffix} ${DEPLOYDIR}
	done

	cp -vf ${RSA_PRIV_KEYS} ${RSA_PUB_KEYS} ${DEPLOYDIR}

	if [ ${OP_TEE} -eq 1 ]; then
		cp -vf ${RSA_PRIV_BL32} ${RSA_PUB_BL32} ${DEPLOYDIR}
	fi
}

do_install:append() {
	install -d ${D}${INSTALL_PATH_SECBOOT_KEYS}

	for suffix in ${BOOT_TYPE}
	do
		install -m 0666 "${BL2_SIGN}-${suffix}" ${D}${INSTALL_PATH_SECBOOT_KEYS}
	done

	install -m 0666 ${RSA_PUB_KEYS} ${D}${INSTALL_PATH_SECBOOT_KEYS}
	if [ ${OP_TEE} -eq 1 ]; then
		install -m 0666 ${RSA_PUB_BL32} ${D}${INSTALL_PATH_SECBOOT_KEYS}
	fi
	install -m 0777 ${SECBOOT_SCRIPT} ${D}${INSTALL_PATH_SECBOOT_KEYS}
}

FILES:${PN}-secboot += "${INSTALL_PATH_SECBOOT_KEYS}${BL2_SIGN_FILE}-*"
FILES:${PN}-secboot += "${INSTALL_PATH_SECBOOT_KEYS}${RSA_PUB_BL2_FILE}"
FILES:${PN}-secboot += "${INSTALL_PATH_SECBOOT_KEYS}${RSA_PUB_BL31_FILE}"
FILES:${PN}-secboot += "${@ "${INSTALL_PATH_SECBOOT_KEYS}${RSA_PUB_BL32_FILE}" if "${OP_TEE}" == '1' else ''}"
FILES:${PN}-secboot += "${INSTALL_PATH_SECBOOT_KEYS}${RSA_PUB_BL33_FILE}"
FILES:${PN}-secboot += "${INSTALL_PATH_SECBOOT_KEYS}${SECBOOT_SCRIPT_FILE}"

PROVIDES:append = " ${PN}-secboot"
PACKAGES:append = " ${PN}-secboot"
