require recipes-extended/xen-examples/xen-examples-dom0less.inc

CFG_NAME:s32cc = "config_s32cc_dom0less"
CFG_NAME:s32n7sim = "config_s32n7_dom0less"

do_compile[depends] += " \
    ${DOM0LESS_ROOTFS}:do_image_complete \
"
