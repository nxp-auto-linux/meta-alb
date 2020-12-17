require xen-nxp.inc

do_deploy_append() {
	# Create relative symbolic link for xen
	if [ -f ${D}/boot/xen ]; then
		cd ${DEPLOYDIR} && ln -sf xen-${MACHINE} ${DEPLOYDIR}/xen && cd -
	fi
}

SRC_URI_append_gen1 += "file://xen_s32gen1.cfg"
SRC_URI_append_s32v2xx += "file://xen_s32v2xx.cfg"
