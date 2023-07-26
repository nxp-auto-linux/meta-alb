# Copyright 2021 NXP

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

PACKAGES =+ "${PN}-llce"
FILES:${PN}-llce = "${base_libdir}/firmware/dte.bin \
			${base_libdir}/firmware/frpe.bin \
			${base_libdir}/firmware/ppe_tx.bin \
			${base_libdir}/firmware/ppe_rx.bin"
