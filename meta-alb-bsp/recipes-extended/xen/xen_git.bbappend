# Copyright 2022-2023,2025 NXP

require xen-nxp_git.inc

# move to SDK
RDEPENDS:${PN} += " ${@bb.utils.contains('DISTRO_FEATURES', 'virtio', 'virtio-disk', '', d)}"

do_deploy:append() {
	# Create relative symbolic link for xen
	cd ${DEPLOYDIR} && ln -sf xen-${MACHINE} ${DEPLOYDIR}/xen && cd -
}

SRC_URI:append:s32cc = " \
			file://xen_s32cc.cfg \
			${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'file://xen_optee.cfg', '', d)} \
			${@bb.utils.contains('DISTRO_FEATURES', 'virtio', 'file://xen_virtio.cfg', '', d)} \
			"

SRC_URI:append:s32n = " \
			file://xen_s32n7.cfg \
			"
