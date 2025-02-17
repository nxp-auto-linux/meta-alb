# Copyright 2025 NXP

LICENSE:append = " & NXP-S32-PLATFORM-SOFTWARE \
"
LIC_FILES_CHKSUM:append = " \
	file://license-carvnet.rtf;md5=bafc2fc12f6ba76334b95f6ea4844b01 \
"
NO_GENERIC_LICENSE[NXP-S32-PLATFORM-SOFTWARE] = "license-carvnet.rtf"


# Copy an CAR-V-NET fw (generic example "rpmsg_echo_irq") to the kernel 
# fw location /lib/firmware and rename to the name expected by nxp-carvnet-rproc driver
CARV_SOURCE_NAME ?= "rpmsg_echo_irq"
CARV_FIRMWARE_NAME ?= "s32n7-carvnet.fw"

SRC_URI:append:s32 = " \
    file://${NXP_FIRMWARE_LOCAL_DIR}/${CARV_SOURCE_NAME} \
    file://${NXP_FIRMWARE_LOCAL_DIR}/license-carvnet.rtf \
"

do_install:append () {
    install -m 0644 ${WORKDIR}/${NXP_FIRMWARE_LOCAL_DIR}/${CARV_SOURCE_NAME} ${D}/${base_libdir}/firmware/${CARV_FIRMWARE_NAME}
}

do_unpack_license_carvnet() {
    cp -vf ${WORKDIR}/${NXP_FIRMWARE_LOCAL_DIR}/license-carvnet.rtf ${WORKDIR}/${BPN}-${PV}/
}

python do_unpack:append() {
    bb.build.exec_func('do_unpack_license_carvnet', d)
}

PACKAGES =+ "${PN}-carvnet"
FILES:${PN}-carvnet = "${base_libdir}/firmware/${CARV_FIRMWARE_NAME}"
