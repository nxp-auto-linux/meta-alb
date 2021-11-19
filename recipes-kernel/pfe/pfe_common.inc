# Copyright 2019-2021 NXP
#
# PFE common recipe for Master/Slave

inherit module

SUMMARY = "Linux driver for the Packet Forwarding Engine hardware"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE-GPL2.txt;md5=5dcdfe25f21119aa5435eab9d0256af7"

PFE_SUPPORTED_REV ?= "PFE_CFG_IP_VERSION_NPU_7_14a"

URL ?= "git://source.codeaurora.org/external/autobsps32/extra/pfeng;protocol=https"
SRC_URI = "${URL}"

SRCREV ?= "e24933f1b4ed3e9e312f2a3c5f4ee081394aa813"

S = "${WORKDIR}/git"
MDIR = "${S}/sw/linux-pfeng"
INSTALL_DIR = "${D}/${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/net/ethernet/nxp/pfe"

EXTRA_OEMAKE_append = " KERNELDIR=${STAGING_KERNEL_DIR} PFE_CFG_IP_VERSION=${PFE_SUPPORTED_REV} MDIR=${MDIR} -C ${MDIR} V=1 drv-build"

COMPATIBLE_MACHINE = "s32g"
