DEPENDS:append = " u-boot-tools-native"
DEPENDS:append = " arm-trusted-firmware-verifiedboot"

do_compile[depends] += "arm-trusted-firmware:do_populate_sysroot"

KERNEL_SUFIX_VALUE = "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', '.initramfs', '', d)}"

do_deploy:append:s32() {
    ESCAPED_DTB_PATH=$(echo ${KERNEL_DEVICETREE} | sed 's/\//\\\//g')
    sed -e "\
              s/<kernel_image>/arch\/arm64\/boot\/Image${KERNEL_SUFIX_VALUE}/g;                 \
              s/<dtb_blob>/arch\/arm64\/boot\/dts\/${ESCAPED_DTB_PATH}/g  \
           " ${RECIPE_SYSROOT}/etc/keys/verifiedboot/s32.its > ${B}/s32_updated.its

    # pack kernel and dts in one file and sign
    mkimage -f ${B}/s32_updated.its -k ${RECIPE_SYSROOT}/etc/keys/verifiedboot/  -r ${WORKDIR}/Image-kernel-signed.itb

    install -m 0644 ${WORKDIR}/Image-kernel-signed.itb ${D}/boot/

    cp ${WORKDIR}/Image-kernel-signed.itb ${DEPLOYDIR}
}

FILES:${PN}-verifiedboot += "/boot/Image-kernel-signed.itb"
PROVIDES += "${PN}-verifiedboot"
PACKAGES += "${PN}-verifiedboot"
