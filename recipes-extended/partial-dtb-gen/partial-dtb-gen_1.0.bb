# Copyright 2021 NXP
#
# Recipe for tool which takes a specified node from Linux DT, modifies
# it and adds it to a separate partial DT, along with its references

SUMMARY = "Recipe for partial-dtb-gen tool, used for generating partial DTS \
    for device passthrough in Dom0less-VMs use-cases"
SECTION = "utils"
DEPENDS = "python3 python3-pip python-fdt"
RDEPENDS:${PN} = "python3 python3-pip python-fdt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

BBCLASSEXTEND = "native"

URL ?= "git://github.com/nxp-auto-linux/partial_dtb_gen;protocol=https"
BRANCH ?= "${RELEASE_BASE}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
"
SRCREV = "9643e82eba5bc69b7881193e84ee91e93c77ee75"

S = "${WORKDIR}/git"

PARTIAL_DTB_GEN_DIR = "partial_dtb_gen"
FILES:${PN} = "${bindir}/${PARTIAL_DTB_GEN_DIR}/*.py"

do_install() {
    install -d ${D}${bindir}/${PARTIAL_DTB_GEN_DIR}
    install -m 0755 "${S}/partial_dtb_gen.py" ${D}${bindir}/${PARTIAL_DTB_GEN_DIR}/
}
