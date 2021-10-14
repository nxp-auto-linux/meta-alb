DESCRIPTION = "Firmware images for PHYs used on NXP boards"
LICENSE = "Freescale-EULA"
LIC_FILES_CHKSUM = "file://EULA;md5=c9ae442cf1f9dd6c13dfad64b0ffe73f"

inherit deploy

S = "${WORKDIR}"

AQR_BIN ??= "aquantia/apps/aq_programming.bin"
AQR_FIRMWARE ??= "aquantia/AQR-G2_v3.3.A-AQR_Freescale_AQR107_ID16066_VER554.cld"

SRC_URI = "\
    file://readme \
    file://EULA \
    file://${AQR_BIN} \
    file://${AQR_FIRMWARE} \
"

UCODE = "\
    ${AQR_BIN} \
    ${AQR_FIRMWARE} \
"
do_install () {
    install -d ${D}/boot
    for name in ${UCODE};do
        install -m 644 ${S}/$name ${D}/boot/
    done
}

do_deploy () {
    install -d ${DEPLOYDIR}/
    for name in ${UCODE};do
        install -m 644 ${S}/$name ${DEPLOYDIR}/
    done
}
addtask deploy before do_build after do_install

PACKAGES += "${PN}-image"
FILES_${PN}-image += "/boot"


COMPATIBLE_MACHINE_append = "ls1046abluebox|ls2084abbmini"
