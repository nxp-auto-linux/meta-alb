# Copyright 2020 NXP
# Copy s32camcc network init script to rootfs

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SUMMARY = "Add custom net init script in rootfs"

# Init IP Link script may be different per MACHINE, but the systemd
# service should be the same
INIT_IP_SOURCE = "init-ip-link-${MACHINE}.sh"
INIT_IP_SCRIPT = "init-ip-link.sh"
INIT_IP_SERVICE = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', \
	'init-ip-link.service', '', d)}"

SRC_URI += " \
    file://${INIT_IP_SOURCE} \
"

SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', \
	'file://${INIT_IP_SERVICE}', '', d)}"

PACKAGES += "${PN}-root"
FILES_${PN} += "/etc"
FILES_${PN}-root += "/init-ip-link.sh"

do_install() {
	# provide two copies of the net init script, one for the single user environment,
	# and another for the multiuser environment
	mkdir -p ${D}/${base_prefix}/etc/init.d
	install -m 755 ${WORKDIR}/${INIT_IP_SOURCE} ${D}/${base_prefix}/${INIT_IP_SCRIPT}
	install -m 755 ${WORKDIR}/${INIT_IP_SOURCE} ${D}/${base_prefix}/etc/init.d/${INIT_IP_SCRIPT}

	if [ -n "${INIT_IP_SERVICE}" ]; then
		# install SystemD service for the multiuser environment
		mkdir -p ${D}/${base_prefix}/etc/systemd/system/multi-user.target.wants
		install -m 755 ${WORKDIR}/${INIT_IP_SERVICE} ${D}/${base_prefix}/etc/systemd/system/${INIT_IP_SERVICE}
		cd ${D}/${base_prefix}/etc/systemd/system/multi-user.target.wants
		ln -s ../${INIT_IP_SERVICE} ./${INIT_IP_SERVICE}
	else
		# install SysV init links for the multiuser environment
		mkdir -p ${D}/${base_prefix}/etc/rc2.d
		mkdir -p ${D}/${base_prefix}/etc/rc3.d
		mkdir -p ${D}/${base_prefix}/etc/rc4.d
		mkdir -p ${D}/${base_prefix}/etc/rc5.d
		cd ${D}/${base_prefix}/etc
		ln -s ../init.d/${INIT_IP_SCRIPT} ./rc2.d/S98net-init.sh
		ln -s ../init.d/${INIT_IP_SCRIPT} ./rc3.d/S98net-init.sh
		ln -s ../init.d/${INIT_IP_SCRIPT} ./rc4.d/S98net-init.sh
		ln -s ../init.d/${INIT_IP_SCRIPT} ./rc5.d/S98net-init.sh
	fi
}

RDEPENDS_${PN} += "bash"
RDEPENDS_${PN}-root += "bash"

COMPATIBLE_MACHINE = "s32v234campp"
