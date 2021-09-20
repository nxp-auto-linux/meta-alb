# Copyright 2021 NXP
#
# This recipe deploys the Cortex-M toolchain in binary format

SUMMARY = "Pre-built Cortex-M7 toolchain"
LICENSE = "GPL-3.0-with-GCC-exception & GPLv3"
LIC_FILES_CHKSUM = "\
    file://${COMMON_LICENSE_DIR}/GPL-3.0-with-GCC-exception;md5=aef5f35c9272f508be848cd99e0151df \
    file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891 \
"

BBCLASSEXTEND = "native"
GCC_NAME = "gcc-arm-none-eabi-10-2020-q4-major"

SRC_URI = "https://developer.arm.com/-/media/Files/downloads/gnu-rm/10-2020q4/${GCC_NAME}-x86_64-linux.tar.bz2;"
SRC_URI[md5sum] = "8312c4c91799885f222f663fc81f9a31"
SRC_URI[sha256sum] = "21134caa478bbf5352e239fbc6e2da3038f8d2207e089efc96c3b55f1edcd618"

S = "${WORKDIR}/${GCC_NAME}"
do_install_append() {
	install -d ${D}${bindir}
	cp -r ${S}/* ${D}${base_prefix}/
}

SYSROOT_PREPROCESS_FUNCS += "extra_gcc_arm_populate_sysroot"
extra_gcc_arm_populate_sysroot() {
	cp -r ${S}/arm-none-eabi ${SYSROOT_DESTDIR}/${base_prefix}
}

INSANE_SKIP_${PN}_append += "already-stripped"
