require xen-nxp.inc

do_deploy_append() {
	# Create relative symbolic link for xen
	cd ${DEPLOYDIR} && ln -sf xen-${MACHINE} ${DEPLOYDIR}/xen && cd -
}

SRC_URI_append_gen1 += "file://xen_s32gen1.cfg"
