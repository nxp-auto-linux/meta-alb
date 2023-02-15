require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot-tools.inc
require recipes-bsp/u-boot/u-boot-src-${PV}.inc

LIC_FILES_CHKSUM = "file://Licenses/README;md5=30503fd321432fc713238f582193b78e"

DEPENDS:append = "${@ ' python3-native python-fdt-native' if d.getVar('SCMI_DTB_NODE_CHANGE') else ''}"

do_install:append() {
	# Switch from the SIUL2 nodes to the SCMI ones
	install -m 0755 ${S}/tools/nxp/scmi_dtb_node_change.py  ${D}${bindir}/scmi_dtb_node_change.py
}

FILES-${PN}-scmi = "${bindir}/scmi_dtb_node_change.py"
PROVIDES += "${PN}-scmi"
PROVIDES:class-native += "u-boot-tools-scmi-native"
