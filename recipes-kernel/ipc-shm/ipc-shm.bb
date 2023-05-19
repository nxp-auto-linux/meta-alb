# SPDX-License-Identifier:	BSD-3-Clause
#
# Copyright 2018-2019 NXP
#

SUMMARY = "Support for Inter-Process(or) Communication over Shared Memory (ipc-shm)"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

inherit module

URL ?= "git://github.com/nxp-auto-linux/ipc-shm;protocol=https"
BRANCH ?= "${RELEASE_BASE}"
SRC_URI = "${URL};branch=${BRANCH}"
SRCREV = "841af092aa30b8660955cf99deeca2ddd7bc4fce"

S = "${WORKDIR}/git"
DESTDIR="${D}"
MODULES_MODULE_SYMVERS_LOCATION = "."

DEMO_IPCF_APPS ?= "sample sample_multi_instance"
EXTRA_OEMAKE:append = " --file ./makefile_samples apps="${DEMO_IPCF_APPS}" INSTALL_DIR=${DESTDIR} KERNELDIR=${KBUILD_OUTPUT} "

PLATFORM_FLAVOR:s32g2 = "s32g2"
PLATFORM_FLAVOR:s32g3 = "s32g3"
PLATFORM_FLAVOR:s32r45evb = "s32r45"
EXTRA_OEMAKE:append = " PLATFORM_FLAVOR=${PLATFORM_FLAVOR} "

# Prevent to load ipc-shm-uio at boot time
KERNEL_MODULE_PROBECONF += "ipc-shm-uio"
module_conf_ipc-shm-uio = "blacklist ipc-shm-uio"
KERNEL_MODULE_PROBECONF += "ipc-sample-multi-instance"
module_conf_ipc-sample-multi-instance = "blacklist ipc-sample-multi-instance"
FILES:${PN} += "${sysconfdir}/modprobe.d/*"
