# Copyright 2021,2023,2025 NXP
#
# Recipe to configure a complete Xen boot flow on various use-cases
inherit deploy

SUMMARY = "Recipe to configure a complete Xen boot flow on various use-cases"
SECTION = "utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS += " imagebuilder-native"
RDEPENDS:${PN} += " xen kernel-devicetree"
FILESEXTRAPATHS:prepend := "${THISDIR}:"

SRC_URI = "file://xen-examples"

S = "${WORKDIR}"
XEN_EXAMPLES_DIR = "${S}/xen-examples"

BIN_PREPEND_PATH = "/boot"
BOOT_SCRIPT = "boot.scr"
BOOT_SOURCE = "boot.source"

EXTRA_CMDS_FILE ?= ""
CFG_NAME ?= ""
EXTRA_CMDS_FILE:s32cc = "extra_cmds_s32cc"
EXTRA_CMDS_FILE:s32n = "extra_cmds_s32n7"

SUPPORTED_BOOT_TYPES:s32n = "${BOOT_TYPE}"

modify_imagebuilder_config() {
    CFG_FILE="$1"

    if [ ! -f "$CFG_FILE" ]; then
        bberror "$CFG_FILE cannot be found."
        exit 1
    fi

    # Append machine-specific linux .dtb file name
    echo "DEVICE_TREE=\"${KERNEL_DEVICETREE_BASENAME}\"" >> "${CFG_FILE}"

    # Append ramdisk image name for each DomU
    NUM_DOMUS=$(grep "^NUM_DOMUS=[0-9]" ${CFG_FILE} | cut -d '=' -f2 | head -n1)
    if [ -z "${NUM_DOMUS}" ]; then
        NUM_DOMUS=0
    fi

    i=0
    while test ${i} -lt ${NUM_DOMUS}; do
        echo "DOMU_RAMDISK[${i}]=\"${DOM0LESS_ROOTFS}-${MACHINE}.cpio.gz\"" >> ${CFG_FILE}
        i=`expr ${i} + 1`;
    done

    # Append file name with extra commands to be added in boot script
    if [ -n "${EXTRA_CMDS_FILE}" ]; then
        echo "APPEND_EXTRA_CMDS=\"${XEN_EXAMPLES_DIR}/${EXTRA_CMDS_FILE}\"" >> "${CFG_FILE}"
    fi
}

do_configure:s32cc() {
    CFG_FILE="${XEN_EXAMPLES_DIR}/${CFG_NAME}"
    modify_imagebuilder_config "${CFG_FILE}"
}

do_configure:s32n() {
    for suffix in ${SUPPORTED_BOOT_TYPES}; do
        CFG_FILE="${XEN_EXAMPLES_DIR}/${CFG_NAME}-${suffix}"
        modify_imagebuilder_config "${CFG_FILE}"
    done
}

do_compile:s32cc() {
    UBOOT_DIR="${DEPLOY_DIR_IMAGE}"
    IMAGEBUILDER_CONFIG="${XEN_EXAMPLES_DIR}/${CFG_NAME}"
    PREPEND_PATH="${BIN_PREPEND_PATH}"

    # Run Imagebuilder script
    uboot-script-gen -c ${IMAGEBUILDER_CONFIG} \
        -d "${UBOOT_DIR}" \
        -p "${PREPEND_PATH}" \
        -s

    mv ${UBOOT_DIR}/${BOOT_SCRIPT} ${UBOOT_DIR}/${BOOT_SOURCE} ${B}/
}

do_compile:s32n() {
    UBOOT_DIR="${DEPLOY_DIR_IMAGE}"
    IMAGEBUILDER_CONFIG="${XEN_EXAMPLES_DIR}/${CFG_NAME}"
    PREPEND_PATH="${BIN_PREPEND_PATH}"

    for suffix in ${SUPPORTED_BOOT_TYPES}; do
        uboot-script-gen -c "${IMAGEBUILDER_CONFIG}-${suffix}" \
            -d "${UBOOT_DIR}" \
            -p "${PREPEND_PATH}" \
            -s

        mv ${UBOOT_DIR}/${BOOT_SCRIPT}-${suffix} ${B}/
        mv ${UBOOT_DIR}/${BOOT_SOURCE}-${suffix} ${B}/
    done
}

do_install:s32cc() {
    install -d ${D}${BIN_PREPEND_PATH}
    install -m 0755 ${BOOT_SCRIPT} ${D}${BIN_PREPEND_PATH}
    install -m 0755 ${BOOT_SOURCE} ${D}${BIN_PREPEND_PATH}
}

do_install:s32n() {
    install -d ${D}${BIN_PREPEND_PATH}
    for suffix in ${SUPPORTED_BOOT_TYPES}; do
        install -m 0755 ${BOOT_SCRIPT}-${suffix} ${D}${BIN_PREPEND_PATH}
        install -m 0755 ${BOOT_SOURCE}-${suffix} ${D}${BIN_PREPEND_PATH}
    done
}

do_deploy() {
    install -d ${DEPLOYDIR}
}

do_deploy:append:s32cc() {
    install -m 0755 ${D}${BIN_PREPEND_PATH}/${BOOT_SCRIPT} ${DEPLOYDIR}/
    install -m 0755 ${D}${BIN_PREPEND_PATH}/${BOOT_SOURCE} ${DEPLOYDIR}/
}

do_deploy:append:s32n() {
    for suffix in ${SUPPORTED_BOOT_TYPES}; do
        install -m 0755 ${D}${BIN_PREPEND_PATH}/${BOOT_SCRIPT}-${suffix} ${DEPLOYDIR}/
        install -m 0755 ${D}${BIN_PREPEND_PATH}/${BOOT_SOURCE}-${suffix} ${DEPLOYDIR}/
    done
}

addtask deploy after do_install before do_build
FILES:${PN} = "${BIN_PREPEND_PATH}/*"

do_compile[depends] += " \
    virtual/kernel:do_deploy \
    xen:do_deploy \
"
