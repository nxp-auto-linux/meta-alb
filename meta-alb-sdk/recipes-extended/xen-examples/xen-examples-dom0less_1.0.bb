require recipes-extended/xen-examples/xen-examples-dom0less.inc

CFG_NAME:s32cc = "config_s32cc_dom0less"

SRC_URI += "file://${CFG_NAME}"

do_compile[depends] += " \
    ${DOM0LESS_ROOTFS}:do_image_complete \
"
