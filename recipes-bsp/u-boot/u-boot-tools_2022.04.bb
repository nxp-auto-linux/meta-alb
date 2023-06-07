require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot-tools.inc
require recipes-bsp/u-boot/u-boot-src-${PV}.inc

DEPENDS += " gnutls openssl util-linux "

LIC_FILES_CHKSUM = "file://Licenses/README;md5=5a7450c57ffe5ae63fd732446b988025"

DEPENDS:append = "${@ ' python3-native python-fdt-native' if d.getVar('SCMI_DTB_NODE_CHANGE') else ''}"

do_compile () {
    oe_runmake -C ${S} tools-only_defconfig O=${B}

    oe_runmake -C ${S} cross_tools NO_SDL=1 O=${B}
}

do_install:append() {
	# Switch from the SIUL2 nodes to the SCMI ones
	install -m 0755 ${S}/tools/nxp/scmi_dtb_node_change.py  ${D}${bindir}/scmi_dtb_node_change.py
}

FILES:${PN}-scmi = "${bindir}/scmi_dtb_node_change.py"
PROVIDES += "${PN}-scmi"
PROVIDES:class-native += "${BPN}-scmi-native"
PACKAGES += "${PN}-scmi"
