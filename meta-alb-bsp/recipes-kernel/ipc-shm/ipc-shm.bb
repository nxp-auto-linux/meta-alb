# SPDX-License-Identifier:	BSD-3-Clause
#
# Copyright 2018-2019, 2023, 2025 NXP
#

SUMMARY = "Support for Inter-Process(or) Communication over Shared Memory (ipc-shm)"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

inherit module

IPC-SHM_INCLUDE_PATH = "recipes-kernel/ipc-shm"
IPC-SHM_INCLUDE:s32g2  = "${IPC-SHM_INCLUDE_PATH}/ipc-shm-s32g2-defs.inc"
IPC-SHM_INCLUDE:s32g3  = "${IPC-SHM_INCLUDE_PATH}/ipc-shm-s32g3-defs.inc"
IPC-SHM_INCLUDE:s32r45 = "${IPC-SHM_INCLUDE_PATH}/ipc-shm-s32r45-defs.inc"
IPC-SHM_INCLUDE:s32r478 = "${IPC-SHM_INCLUDE_PATH}/ipc-shm-s32r478-defs.inc"
IPC-SHM_INCLUDE:s32n7sim = "${IPC-SHM_INCLUDE_PATH}/ipc-shm-s32n7sim-defs.inc"
require ${@d.getVar('IPC-SHM_INCLUDE') or ''}

URL ?= "git://github.com/nxp-auto-linux/ipc-shm;protocol=https"
SRC_URI = "${URL};branch=${BRANCH}"

S = "${WORKDIR}/git"
DESTDIR="${D}"
MODULES_MODULE_SYMVERS_LOCATION = "."

DEMO_IPCF_KERNEL_APPS ?= "sample sample_multi_instance"
EXTRA_OEMAKE:append = " -f ./makefile_samples apps="${DEMO_IPCF_KERNEL_APPS}" INSTALL_DIR=${DESTDIR} KERNELDIR=${KBUILD_OUTPUT} "
EXTRA_OEMAKE:append = " PLATFORM_FLAVOR=${PLATFORM_FLAVOR} "

# User-space apps
DEMO_IPCF_USER_APPS ?= "sample_user sample_multi_instance_user"

# Building user space examples
do_compile:append() {
    # Use target sysroot for user-space apps
    export CC="${CC}"
    export CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
    export LDFLAGS="${LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"
    export IPC_UIO_MODULE_DIR=${nonarch_base_libdir}/modules/${KERNEL_VERSION}/updates

    oe_runmake -f ./makefile_samples \
        apps="${DEMO_IPCF_USER_APPS}" \
        PLATFORM_FLAVOR=${PLATFORM_FLAVOR} \
        clean

    oe_runmake -f ./makefile_samples \
        apps="${DEMO_IPCF_USER_APPS}" \
        PLATFORM_FLAVOR=${PLATFORM_FLAVOR} \
        all
}

do_install:append() {
    install -d ${D}${bindir}/

    for app in ${DEMO_IPCF_USER_APPS}; do
        install -m 0755 ${S}/$app/*.elf ${D}${bindir}/
    done
}

# Prevent to load ipc-shm-uio at boot time
KERNEL_MODULE_PROBECONF += "ipc-shm-uio"
module_conf_ipc-shm-uio = "blacklist ipc-shm-uio"
KERNEL_MODULE_PROBECONF += "ipc-sample-multi-instance"
module_conf_ipc-sample-multi-instance = "blacklist ipc-sample-multi-instance"

FILES:${PN} += "${sysconfdir}/modprobe.d/* ${bindir}/*"

# Supported platforms
COMPATIBLE_MACHINE = "^(s32g|s32r45|s32r478|s32n7sim)$"
