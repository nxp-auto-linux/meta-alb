SRC_URI = "git://source.codeaurora.org/external/autobsps32/xen;protocol=https;branch=alb/master"

SRCREV = "6e6506367da2f447269d4c01157dc4cb9a0b7fd0"

S = "${WORKDIR}/git"

EXTRA_OEMAKE += "CONFIG_DEBUG=y"
EXTRA_OEMAKE_append_gen1 += "CONFIG_EARLY_PRINTK=s32gen1"
EXTRA_OEMAKE_append_s32v2xx += "CONFIG_EARLY_PRINTK=s32v2xx"

do_deploy_append() {
	# Create relative symbolic link for xen
	if [ -f ${D}/boot/xen ]; then
		cd ${DEPLOYDIR} && ln -sf xen-${MACHINE} ${DEPLOYDIR}/xen && cd -
	fi
}
