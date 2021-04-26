# Copyright 2021 NXP
#
# Recipe to compile partial device trees used for Xen passthrough
inherit deploy

SUMMARY = "Recipe to compile partial device trees used for Xen passthrough"
SECTION = "utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "dtc-native"
FILESEXTRAPATHS_prepend = "${THISDIR}/${BPN}:"
COMPATIBLE_MACHINE = "s32g2evb"

SRC_URI += " \
    file://passthrough_gmac_s32g274aevb.dts \
"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

FILES_${PN} += "/boot/*.dtb"

do_compile() {
    cd ${S}
    # Compile partial .dts files
    for dts_name in *.dts; do
        if [ -f ${dts_name} ]; then
            output_dtb_name=${dts_name%.*}
            dtc -O dtb -o ${B}/${output_dtb_name}.dtb ${dts_name}
        fi
    done
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