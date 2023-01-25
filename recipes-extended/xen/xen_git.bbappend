# Copyright 2022 NXP

require xen-nxp_git.inc

do_deploy:append() {
	# Create relative symbolic link for xen
	cd ${DEPLOYDIR} && ln -sf xen-${MACHINE} ${DEPLOYDIR}/xen && cd -
}

SRC_URI:append:s32cc = " file://xen_s32cc.cfg"
