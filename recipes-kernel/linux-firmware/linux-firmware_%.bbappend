# Copyright 2021 NXP

NXP_FIRMWARE_LOCAL_DIR ?= "."

SRC_URI += "file://${NXP_FIRMWARE_LOCAL_DIR}/dte.bin \
	    file://${NXP_FIRMWARE_LOCAL_DIR}/frpe.bin \
	    file://${NXP_FIRMWARE_LOCAL_DIR}/ppe_tx.bin \
	    file://${NXP_FIRMWARE_LOCAL_DIR}/ppe_rx.bin"

do_install_append () {
	if ${@bb.utils.contains('DISTRO_FEATURES', 'llce-can', 'true', 'false', d)}; then
		install -m 0644 ${WORKDIR}/${NXP_FIRMWARE_LOCAL_DIR}/dte.bin ${D}/lib/firmware/dte.bin
		install -m 0644 ${WORKDIR}/${NXP_FIRMWARE_LOCAL_DIR}/frpe.bin ${D}/lib/firmware/frpe.bin
		install -m 0644 ${WORKDIR}/${NXP_FIRMWARE_LOCAL_DIR}/ppe_tx.bin ${D}/lib/firmware/ppe_tx.bin
		install -m 0644 ${WORKDIR}/${NXP_FIRMWARE_LOCAL_DIR}/ppe_rx.bin ${D}/lib/firmware/ppe_rx.bin
	fi
}

PACKAGES =+ "${PN}-llce-can"
FILES_${PN}-llce-can = "/lib/firmware/dte.bin \
			/lib/firmware/frpe.bin \
			/lib/firmware/ppe_tx.bin \
			/lib/firmware/ppe_rx.bin"
