# Copyright 2023,2024,2025 NXP

DEPENDS:append = " arm-trusted-firmware-tools-native"

FIT_KEYS_DIR = "${WORKDIR}/keys_fit"
RSA_PRIV_FIT = "boot_key.key"
RSA_PUB_FIT = "boot_key.crt"
ITS = "s32.its"
INSTALL_PATH_FIT_KEYS = "/etc/keys/verifiedboot/"
DTB_DEBUG = "${@oe.utils.conditional('BUILD_TYPE', 'debug', 'DEBUG=1', '', d)}"

SRC_URI:append = " \
                    file://s32.its \
                 "
SYSROOT_DIRS:append  = " \
                           /etc/keys/verifiedboot/ \
                       "

do_generate_fit_keys () {
        # generating a pair of keys
        mkdir -p ${FIT_KEYS_DIR}
        openssl genrsa -out ${FIT_KEYS_DIR}/${RSA_PRIV_FIT} 2048
        openssl req -batch -new -x509 -key  ${FIT_KEYS_DIR}/${RSA_PRIV_FIT} -out ${FIT_KEYS_DIR}/${RSA_PUB_FIT}

        # creating TFA dtbs
        make -C "${S}" CROSS_COMPILE=${TARGET_PREFIX} ARCH=${TARGET_ARCH} BUILD_BASE=${B} PLAT=${TFA_PLAT} ${DTB_DEBUG} dtbs

        # workaround: using an empty file as kernel and dtb because mkimage does not allow only updating atf dtb with public key
        touch ${WORKDIR}/empty_image
        sed -e "s/<kernel_image>/empty_image/g;s/<dtb_blob>/empty_image/g" ${WORKDIR}/${ITS} > ${WORKDIR}/s32_updated.its

        # updating atf dtb with public key. This will be used at boot by bootloader to verify kernel signature
        mkimage -f ${WORKDIR}/s32_updated.its  -K ${TFA_BINARIES}/fdts/$(TFA_DEVICETREE)  -k ${FIT_KEYS_DIR}  -r ${WORKDIR}/dummy_output
}

do_generate_fit_keys[depends] += "openssl-native:do_populate_sysroot"
addtask do_generate_fit_keys before do_compile after do_configure

do_install:append() {
       install -d ${D}${INSTALL_PATH_FIT_KEYS}
       install -m 0644 ${FIT_KEYS_DIR}/${RSA_PUB_FIT} ${D}${INSTALL_PATH_FIT_KEYS}
       install -m 0600 ${FIT_KEYS_DIR}/${RSA_PRIV_FIT} ${D}${INSTALL_PATH_FIT_KEYS}
       install -m 0644 ${WORKDIR}/${ITS} ${D}${INSTALL_PATH_FIT_KEYS}
}

FILES:${PN}-verifiedboot += "${INSTALL_PATH_FIT_KEYS}${RSA_PUB_FIT}"
FILES:${PN}-verifiedboot += "${INSTALL_PATH_FIT_KEYS}${RSA_PRIV_FIT}"
FILES:${PN}-verifiedboot += "${INSTALL_PATH_FIT_KEYS}${ITS}"
PROVIDES:append = " ${PN}-verifiedboot"
PACKAGES:append = " ${PN}-verifiedboot"

FILES:${PN}-verifiedboot-pubkey += "${INSTALL_PATH}${RSA_PUB_FIT}"
PACKAGES:append = " ${PN}-verifiedboot-pubkey"
