# Copyright 2021 NXP
#
# This recipe deploys the Cortex-M toolchain in binary format

SUMMARY = "Pre-built Cortex-M7 toolchain"
LICENSE = "GPL-3.0-with-GCC-exception & GPL-3.0-only"
LIC_FILES_CHKSUM = "\
    file://${COMMON_LICENSE_DIR}/GPL-3.0-with-GCC-exception;md5=aef5f35c9272f508be848cd99e0151df \
    file://${COMMON_LICENSE_DIR}/GPL-3.0-only;md5=c79ff39f19dfec6d293b95dea7b07891 \
"

BBCLASSEXTEND = "native"
GCC_NAME = "gcc-arm-none-eabi-10.3-2021.10"
GCC_RELEASE = "10.3-2021.10"

SRC_URI = "https://developer.arm.com/-/media/Files/downloads/gnu-rm/${GCC_RELEASE}/${GCC_NAME}-x86_64-linux.tar.bz2;"
SRC_URI[md5sum] = "2383e4eb4ea23f248d33adc70dc3227e"
SRC_URI[sha256sum] = "97dbb4f019ad1650b732faffcc881689cedc14e2b7ee863d390e0a41ef16c9a3"

S = "${WORKDIR}/${GCC_NAME}"
do_install:append() {
	install -d ${D}${bindir}
	cp -r ${S}/* ${D}${base_prefix}/
}

SYSROOT_PREPROCESS_FUNCS += "extra_gcc_arm_populate_sysroot"
extra_gcc_arm_populate_sysroot() {
	cp -r ${S}/arm-none-eabi ${SYSROOT_DESTDIR}/${base_prefix}
}

INSANE_SKIP:${PN}:append = " already-stripped"
