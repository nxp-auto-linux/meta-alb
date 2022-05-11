# Copyright 2019-2022 NXP
#
# PFE common recipe for Master/Slave

inherit module

SUMMARY = "Linux driver for the Packet Forwarding Engine hardware"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE-GPL2.txt;md5=5dcdfe25f21119aa5435eab9d0256af7"

URL ?= "git://source.codeaurora.org/external/autobsps32/extra/pfeng;protocol=https"
SRC_URI = "${URL}"

SRCREV ?= "daa1420e194171a298995a2cd926f75c6c70b44b"

S = "${WORKDIR}/git"
MDIR = "${S}/sw/linux-pfeng"
INSTALL_DIR = "${D}/${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/net/ethernet/nxp/pfe"

EXTRA_OEMAKE_append = " KERNELDIR=${STAGING_KERNEL_DIR} MDIR=${MDIR} -C ${MDIR} V=1 drv-build"

COMPATIBLE_MACHINE = "s32g"
