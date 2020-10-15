FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

URL ?= "git://source.codeaurora.org/external/autobsps32/xen;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${MAJ_VER}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
    file://0001-xen-trace-Fix-gcc9-alignment-warning.patch \
"

SRCREV = "50f5b9b385b541a7cc30411cb7e8391d379f461b"

S = "${WORKDIR}/git"

EXTRA_OEMAKE += "CONFIG_DEBUG=y"
EXTRA_OEMAKE_append_gen1 += "CONFIG_EARLY_PRINTK=s32gen1"
EXTRA_OEMAKE_append_s32v2xx += "CONFIG_EARLY_PRINTK=s32v2xx"

FILES_${PN}-scripts-common += " ${sysconfdir}/xen/*.cfg"

addtask deploy after do_install
do_deploy_append() {
	# Create relative symbolic link for xen
	if [ -f ${D}/boot/xen ]; then
		cd ${DEPLOYDIR} && ln -sf xen-${MACHINE} ${DEPLOYDIR}/xen && cd -
	fi
}
