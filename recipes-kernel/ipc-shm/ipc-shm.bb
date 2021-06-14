# SPDX-License-Identifier:	BSD-3-Clause
#
# Copyright 2018-2019 NXP
#

SUMMARY = "Support for Inter-Process(or) Communication over Shared Memory (ipc-shm)"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

inherit module

URL ?= "git://source.codeaurora.org/external/autobsps32/ipcf/ipc-shm;protocol=https"
BRANCH ?= "${RELEASE_BASE}"
SRC_URI = "${URL};branch=${BRANCH}"
SRCREV = "56d3710e01052a94ad9b6f559e0390d77c059277"

S = "${WORKDIR}/git"
DESTDIR="${D}"
EXTRA_OEMAKE_append = " -C ./sample INSTALL_DIR=${DESTDIR} KERNELDIR=${KBUILD_OUTPUT} "
MODULES_MODULE_SYMVERS_LOCATION = "."

PROVIDES += "kernel-module-ipc-shm-sample${KERNEL_MODULE_PACKAGE_SUFFIX}"
RPROVIDES_${PN} += "kernel-module-ipc-shm-sample${KERNEL_MODULE_PACKAGE_SUFFIX}"
PROVIDES += "kernel-module-ipc-shm-dev${KERNEL_MODULE_PACKAGE_SUFFIX}"
RPROVIDES_${PN} += "kernel-module-ipc-shm-dev${KERNEL_MODULE_PACKAGE_SUFFIX}"
PROVIDES += "kernel-module-ipc-shm-uio${KERNEL_MODULE_PACKAGE_SUFFIX}"
RPROVIDES_${PN} += "kernel-module-ipc-shm-uio${KERNEL_MODULE_PACKAGE_SUFFIX}"

# Prevent to load ipc-shm-uio at boot time
KERNEL_MODULE_PROBECONF += "ipc-shm-uio"
module_conf_ipc-shm-uio = "blacklist ipc-shm-uio"

FILES_${PN} += "${base_libdir}/*"
FILES_${PN} += "${sysconfdir}/modprobe.d/*"
