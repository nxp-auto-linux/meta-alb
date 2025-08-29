require recipes-extended/xen-examples/xen-examples.inc

FILESEXTRAPATHS:prepend := "${THISDIR}:"

SRC_URI += "file://xen-examples-dom0less"

do_post_unpack() {
    cp -vf ${WORKDIR}/xen-examples-dom0less/* ${WORKDIR}/xen-examples/
}

addtask post_unpack after do_unpack before do_patch
