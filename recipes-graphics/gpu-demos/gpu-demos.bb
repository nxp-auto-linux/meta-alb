# Copyright 2018-2019 NXP

inherit autotools
inherit fsl-auto-eula-unpack pkgconfig

DESCRIPTION = "GPU demos as source code"
SECTION = "libs"
LICENSE = "GPL-2.0"

DEPENDS += "mesa gpu-viv-bin-s32v2xx"
RDEPENDS_${PN} += "kernel-module-galcore gpu-viv-bin-s32v2xx bash"

PROVIDES = "gpu-demos"
RPROVIDES_${PN} = "gpu-demos"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

PE = "1"

SRC_URI[md5sum] = "aa0fd134249c113daf977e2300ea4394"
FSL_LOCAL_MIRROR ?= "file://."

KERNEL_NAME = "${PREFERRED_PROVIDER_virtual/kernel}"
KERNEL_VER = '${@d.getVar("PREFERRED_VERSION_${KERNEL_NAME}",True)}'
GPU_VIV_VERSION = "${@oe.utils.conditional('KERNEL_VER', '5.4', '6.4.0.p2', '6.2.4.p4', d)}"

URL ?= "git://source.codeaurora.org/external/autobsps32/gpu;protocol=https"
BRANCH ?= "${RELEASE_BASE}"
SRC_URI = "${URL};branch=${BRANCH} \
           ${FSL_LOCAL_MIRROR}/Vivante_userspace_libraries_and_demos/gpu-viv-bin-s32v234-${GPU_VIV_VERSION}-hardfp.run;fsl-eula=true \
          "
SRCREV = "926c8e57ec103740ede3a8ad9c264e8cbc8c03fc"

B = "${WORKDIR}/git"
S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://${S}/test/sdk/inc/gc_sdk.h;endline=26;md5=e4ea72ff91665efb91435921a29148b7"

EXTRA_OEMAKE = 'CROSS_COMPILE=${TARGET_PREFIX} V=1'

do_compile_prepend() {
    export VIVANTE_USERSPACE="${WORKDIR}/gpu-viv-bin-s32v234-${GPU_VIV_VERSION}-hardfp"
    export ORIG_LFLAGS=" --sysroot=${PKG_CONFIG_SYSROOT_DIR} ${LDFLAGS}"
    export ORIG_PFLAGS=" --sysroot=${PKG_CONFIG_SYSROOT_DIR} ${LDFLAGS}"
    export CXXFLAGS=" --sysroot=${PKG_CONFIG_SYSROOT_DIR} ${CXXFLAGS} "
    export CFLAGS=" --sysroot=${PKG_CONFIG_SYSROOT_DIR} ${CFLAGS} "
}

do_install () {
	oe_runmake DEPMOD=echo MODLIB="${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}" \
	           CC="${KERNEL_CC}" LD="${KERNEL_LD}"

    install -d ${D}/opt/gpu_demos
    install -d ${D}/usr/include

    cp -axr ${S}/* ${D}/opt/gpu_demos
    cp -axr ${VIVANTE_USERSPACE}/usr/include ${D}/usr/include
}

QAPATHTEST[host-user-contaminated] = ""

FILES_${PN} += "/opt/gpu_demos"
FILES_${PN} += "/usr/include"

INSANE_SKIP_${PN} += "staticdev"
INSANE_SKIP_${PN} += "already-stripped"

COMPATIBLE_MACHINE-samples = "(s32v234evb)|(s32v234pcie)|(s32v234bbmini)|(s32v234evb28899)|(s32v234sbc)"
