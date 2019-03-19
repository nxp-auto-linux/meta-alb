SRC_URI = "git://source.codeaurora.org/external/autobsps32/xen;protocol=https;branch=alb/master"

SRCREV = "ad96fd782903f3569875c19ada6ecd2a2f45316b"

S = "${WORKDIR}/git"

EXTRA_OEMAKE += "CONFIG_DEBUG=y"
EXTRA_OEMAKE += "CONFIG_EARLY_PRINTK=s32v"

do_deploy_append() {
	# Create relative symbolic link for xen
	if [ -f ${D}/boot/xen ]; then
		cd ${DEPLOYDIR} && ln -sf xen-${MACHINE} ${DEPLOYDIR}/xen && cd -
	fi
}
