FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
    file://0001-dpdk-extras-Added-support-for-LS2084A.patch \
"

COMPATIBLE_MACHINE_append = "|(ls2080a|ls2084a)"
