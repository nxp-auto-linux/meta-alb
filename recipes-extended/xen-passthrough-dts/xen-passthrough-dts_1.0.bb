# Copyright 2021 NXP
#
# Recipe to compile partial device trees used for Xen passthrough
inherit deploy

SUMMARY = "Recipe to compile partial device trees used for Xen passthrough"
SECTION = "utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "dtc-native partial-dtb-gen-native"
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://template.dts \
    file://template_gmac.dts \
"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

FILES:${PN} += "/boot/*.dtb"
PARTIAL_DTB_GEN = "partial_dtb_gen.py"
PARTIAL_DTB_GEN_DIR = "partial_dtb_gen"

TEMPLATE_FILE_BASENAME = "template"
TEMPLATE_DTS ?= "${TEMPLATE_FILE_BASENAME}.dts"
TEMPLATE_DTB ?= "${TEMPLATE_FILE_BASENAME}.dtb"

PASSTHROUGH_FILE_BASENAME = "passthrough"
PASSTHROUGH_DTS = "${PASSTHROUGH_FILE_BASENAME}.dts"
PASSTHROUGH_DTB = "${PASSTHROUGH_FILE_BASENAME}.dtb"

# Override with xen-example specific variables
include ${XEN_EXAMPLE}.inc

do_compile() {
    cd ${S}

    # PASSTHROUGH_NODE variable is mandatory
    if [ -z "${PASSTHROUGH_NODE}" ]; then
        bberror "PASSTHROUGH_NODE variable should not be empty! Please set it in conf/local.conf."
    fi

    # Compile template dts
    dtc -O dtb -o ${B}/${TEMPLATE_DTB} ${TEMPLATE_DTS}

    # Generate passthrough dts file
    LINUX_DTB_NAME="$(basename ${KERNEL_DEVICETREE})"
    nativepython3 ${STAGING_BINDIR_NATIVE}/${PARTIAL_DTB_GEN_DIR}/${PARTIAL_DTB_GEN} \
        -i "${DEPLOY_DIR_IMAGE}/${LINUX_DTB_NAME}" \
        -t "${B}/${TEMPLATE_DTB}" \
        -n "${PASSTHROUGH_NODE}"

    mv -vf ${PASSTHROUGH_DTS} ${B}/
    rm -vf ${B}/${TEMPLATE_DTB}

    # Compile partial dts
    dtc -O dtb -o ${B}/${PASSTHROUGH_DTB} ${B}/${PASSTHROUGH_DTS}
    cd -
}

do_install() {
    install -d ${D}/boot
    for dtb_name in *.dtb; do
        if [ -f ${dtb_name} ]; then
            install -m 0644 ${dtb_name} ${D}/boot/
        fi
    done
}

do_deploy() {
    # Deploy compiled .dts files to DEPLOYDIR
    for dtb_name in ${B}/*.dtb; do
        install -m 0644 ${dtb_name} ${DEPLOYDIR}/
    done
}

addtask do_deploy after do_compile before do_build
do_compile[depends] += " \
    virtual/kernel:do_deploy \
"