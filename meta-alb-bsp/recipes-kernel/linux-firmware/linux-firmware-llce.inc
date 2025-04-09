# Copyright 2025 NXP

LICENSE:append = " & NXP-S32-PLATFORM-SOFTWARE \
"
LIC_FILES_CHKSUM:append = " \
	file://license-llce.rtf;md5=97a0abc6fb5eb52a8cd1e99205caa71b \
"
NO_GENERIC_LICENSE[NXP-S32-PLATFORM-SOFTWARE] = "license-llce.rtf"
FILESEXTRAPATHS:prepend := "${NXP_FIRMWARE_LOCAL_DIR}:"

# linux-firmware may be included by other recipes as well, since it
# originates in poky, so make sure that we don't break default
# functionality
SRC_URI:append = " \
    ${@f"""file://dte.bin """ if os.path.exists(f"""file://dte.bin""") else ''} \
    ${@f"""file://frpe.bin """ if os.path.exists(f"""file://frpe.bin""") else ''} \
    ${@f"""file://ppe_tx.bin """ if os.path.exists(f"""file://ppe_tx.bin""") else ''} \
    ${@f"""file://ppe_rx.bin """ if os.path.exists(f"""file://ppe_rx.bin""") else ''} \
    ${@f"""file://license-llce.rtf """ if os.path.exists(f"""file://license-llce.rtf""") else ''} \
"

do_install:append () {
    install -m 0644 ${NXP_FIRMWARE_LOCAL_DIR}/dte.bin ${D}/${base_libdir}/firmware/dte.bin
    install -m 0644 ${NXP_FIRMWARE_LOCAL_DIR}/frpe.bin ${D}/${base_libdir}/firmware/frpe.bin
    install -m 0644 ${NXP_FIRMWARE_LOCAL_DIR}/ppe_tx.bin ${D}/${base_libdir}/firmware/ppe_tx.bin
    install -m 0644 ${NXP_FIRMWARE_LOCAL_DIR}/ppe_rx.bin ${D}/${base_libdir}/firmware/ppe_rx.bin
}

do_unpack_license() {
	cp -vf ${NXP_FIRMWARE_LOCAL_DIR}/license-llce.rtf ${WORKDIR}/${BPN}-${PV}/
}

python do_unpack:append() {
    bb.build.exec_func('do_unpack_license', d)
}

PACKAGES =+ "${PN}-llce"
FILES:${PN}-llce = "${base_libdir}/firmware/dte.bin \
			${base_libdir}/firmware/frpe.bin \
			${base_libdir}/firmware/ppe_tx.bin \
			${base_libdir}/firmware/ppe_rx.bin"
