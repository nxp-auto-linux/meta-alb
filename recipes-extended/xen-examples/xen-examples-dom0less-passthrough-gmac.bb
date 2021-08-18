require recipes-extended/xen-examples/xen-examples.inc

CFG_NAME = "config_s32_dom0less_passthrough_gmac"

SRC_URI = "file://config_s32_dom0less_passthrough_gmac"

RDEPENDS_${PN} += " xen-passthrough-dts"

do_compile[depends] += " \
    ${DOM0LESS_ROOTFS}:do_image_complete \
    xen-passthrough-dts:do_deploy \
"