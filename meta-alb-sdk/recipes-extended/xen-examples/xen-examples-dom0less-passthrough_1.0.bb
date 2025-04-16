require recipes-extended/xen-examples/xen-examples-dom0less.inc

CFG_NAME:s32cc = "config_s32cc_dom0less_passthrough"

SRC_URI += "file://${CFG_NAME}"

RDEPENDS:${PN} += " xen-passthrough-dts"

do_compile[depends] += " \
    ${DOM0LESS_ROOTFS}:do_image_complete \
    xen-passthrough-dts:do_deploy \
"

COMPATIBLE_MACHINE = "s32cc"
