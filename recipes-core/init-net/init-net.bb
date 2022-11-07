# Copyright 2020 NXP
# Copy s32camcc network init script to rootfs

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SUMMARY = "Add custom net init script in rootfs"

INIT_IP_LINK = "init-ip-link-${MACHINE}.sh"

SRC_URI += " \
    file://${INIT_IP_LINK} \
"

PACKAGES += "${PN}-root"
FILES:${PN} += "/etc"
FILES:${PN}-root += "/init-ip-link.sh"

do_install() {
	install -m 755 ${WORKDIR}/${INIT_IP_LINK} ${D}/${base_prefix}/init-ip-link.sh

	mkdir -p ${D}/${base_prefix}/etc/init.d
	mkdir -p ${D}/${base_prefix}/etc/rc2.d
	mkdir -p ${D}/${base_prefix}/etc/rc3.d
	mkdir -p ${D}/${base_prefix}/etc/rc4.d
	mkdir -p ${D}/${base_prefix}/etc/rc5.d
	install -m 755 ${WORKDIR}/${INIT_IP_LINK} ${D}/${base_prefix}/etc/init.d/init-ip-link.sh
	cd ${D}/${base_prefix}/etc
	ln -s ../init.d/init-ip-link.sh ./rc2.d/S98net-init.sh
	ln -s ../init.d/init-ip-link.sh ./rc3.d/S98net-init.sh
	ln -s ../init.d/init-ip-link.sh ./rc4.d/S98net-init.sh
	ln -s ../init.d/init-ip-link.sh ./rc5.d/S98net-init.sh
}

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN}-root += "bash"

COMPATIBLE_MACHINE = "s32g274abluebox3"
