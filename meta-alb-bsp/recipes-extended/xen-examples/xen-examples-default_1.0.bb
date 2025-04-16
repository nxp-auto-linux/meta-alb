require recipes-extended/xen-examples/xen-examples.inc

CFG_NAME:s32cc = "config_s32cc_default"
CFG_NAME:s32n7sim = "config_s32n7_default"

SRC_URI += "file://${CFG_NAME}"
