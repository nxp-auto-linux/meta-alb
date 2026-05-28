FILESEXTRAPATHS:prepend := "${THISDIR}:"

SRC_URI:append:s32n = " file://xen-examples-hw-partition"

do_post_unpack_hw_partition() {
    cp -vf ${WORKDIR}/xen-examples-hw-partition/* ${WORKDIR}/xen-examples/
}
