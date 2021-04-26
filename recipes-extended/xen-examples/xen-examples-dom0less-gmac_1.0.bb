require recipes-extended/xen-examples/xen-examples.inc

CFG_NAME = "config_s32g274aevb_dom0less_gmac"

SRC_URI = "file://config_s32g274aevb_dom0less_gmac"

RDEPENDS_${PN} += " xen-passthrough-dts"

do_compile[depends] += " \
    ${DOM0LESS_ROOTFS}:do_image_complete \
    xen-passthrough-dts:do_deploy \
"