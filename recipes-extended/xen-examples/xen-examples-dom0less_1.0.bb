require recipes-extended/xen-examples/xen-examples.inc

CFG_NAME = "config_s32_dom0less"

SRC_URI = "file://config_s32_dom0less"

do_compile[depends] += " \
    ${DOM0LESS_ROOTFS}:do_image_complete \
"