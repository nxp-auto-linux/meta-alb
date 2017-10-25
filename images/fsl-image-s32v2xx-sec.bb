# Copyright 2017 NXP
SUMMARY = "A FIT image comprising the Linux image and dtb"
LICENSE = "MIT"

KERNEL_IMAGE ?= "${KERNEL_IMAGETYPE}"
KERNEL_ITS ?= "s32v234-image-sec.its"
DTB_IMAGE ?= "${KERNEL_IMAGETYPE}-`basename ${KERNEL_DEVICETREE}`"

SRC_URI = "file://${KERNEL_ITS}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit deploy

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
do_populate_sysroot[noexec] = "1"
do_package[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_package_write_rpm[noexec] = "1"

do_fetch[nostamp] = "1"
do_unpack[nostamp] = "1"
do_deploy[nostamp] = "1"
do_deploy[depends] += "u-boot-mkimage-native:do_build virtual/kernel:do_build"

do_deploy () {
    install -d ${DEPLOYDIR}
    rm -f ${KERNEL_IMAGE}
    cp ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGE} .

    cp ${WORKDIR}/${KERNEL_ITS} ${KERNEL_ITS}
    sed -i -e "s,Image,${KERNEL_IMAGE}," ${KERNEL_ITS}
    sed -i -e "s,s32v234.dtb,${DEPLOY_DIR_IMAGE}/${DTB_IMAGE}," ${KERNEL_ITS}

    mkimage -f ${KERNEL_ITS} fsl-image-s32v2xx-sec.itb

    install -m 644 fsl-image-s32v2xx-sec.itb ${DEPLOYDIR}/fsl-image-s32v2xx-sec-${MACHINE}-${DATETIME}.itb
    ln -sf fsl-image-s32v2xx-sec-${MACHINE}-${DATETIME}.itb ${DEPLOYDIR}/fsl-image-s32v2xx-sec-${MACHINE}.itb
}
addtask deploy before do_build after do_unpack

COMPATIBLE_MACHINE = "(s32v234evb|s32v234pcie|s32v234tmdp|s32v234bbmini)"
