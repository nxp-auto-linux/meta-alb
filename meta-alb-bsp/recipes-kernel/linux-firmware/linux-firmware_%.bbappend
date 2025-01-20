# Copyright 2021,2025 NXP

LICENSE:append = " & NXP-S32-PLATFORM-SOFTWARE \
"
LIC_FILES_CHKSUM:append = " \
	file://license-llce.rtf;md5=97a0abc6fb5eb52a8cd1e99205caa71b \
"
NO_GENERIC_LICENSE[NXP-S32-PLATFORM-SOFTWARE] = "license-llce.rtf"

NXP_FIRMWARE_LOCAL_DIR ?= "."

# linux-firmware may be included by other recipes as well, since it
# originates in poky, so make sure that we don't break default
# functionality
SRC_URI:append:s32 = " \
	${@bb.utils.contains('DISTRO_FEATURES', 'llce-fw-load', ' \
		file://${NXP_FIRMWARE_LOCAL_DIR}/dte.bin \
		file://${NXP_FIRMWARE_LOCAL_DIR}/frpe.bin \
		file://${NXP_FIRMWARE_LOCAL_DIR}/ppe_tx.bin \
		file://${NXP_FIRMWARE_LOCAL_DIR}/ppe_rx.bin \
		file://${NXP_FIRMWARE_LOCAL_DIR}/license-llce.rtf \
	', '', d)} \
"

do_install:append () {
	if ${@bb.utils.contains('DISTRO_FEATURES', 'llce-fw-load', 'true', 'false', d)}; then
		install -m 0644 ${WORKDIR}/${NXP_FIRMWARE_LOCAL_DIR}/dte.bin ${D}/${base_libdir}/firmware/dte.bin
		install -m 0644 ${WORKDIR}/${NXP_FIRMWARE_LOCAL_DIR}/frpe.bin ${D}/${base_libdir}/firmware/frpe.bin
		install -m 0644 ${WORKDIR}/${NXP_FIRMWARE_LOCAL_DIR}/ppe_tx.bin ${D}/${base_libdir}/firmware/ppe_tx.bin
		install -m 0644 ${WORKDIR}/${NXP_FIRMWARE_LOCAL_DIR}/ppe_rx.bin ${D}/${base_libdir}/firmware/ppe_rx.bin
	fi
}

do_unpack_license() {
	cp -vf ${WORKDIR}/${NXP_FIRMWARE_LOCAL_DIR}/license-llce.rtf ${WORKDIR}/${BPN}-${PV}/
}

python do_unpack:append() {
    bb.build.exec_func('do_unpack_license', d)
}

PACKAGES =+ "${PN}-llce"
FILES:${PN}-llce = "${base_libdir}/firmware/dte.bin \
			${base_libdir}/firmware/frpe.bin \
			${base_libdir}/firmware/ppe_tx.bin \
			${base_libdir}/firmware/ppe_rx.bin"
