# Copyright (C) 2013 Freescale Semiconductor
# Released under the MIT license (see COPYING.MIT for the terms)
# Copyright 2017 NXP

inherit module

DESCRIPTION = "GPU kernel module for s32v2xx"
SECTION = "libs"
LICENSE = "GPL-2.0"

PROVIDES += "kernel-module-galcore"
SRC_URI = "${FSL_LOCAL_MIRROR}/gpu_drivers/Galcore_kernel_module/galcore_s32v234_6.2.2.tar.gz \
	   file://COPYING \
	   file://Makefile \
	   file://build.sh \
	  "

LIC_FILES_CHKSUM = "file://COPYING;md5=6731edef2727e51a061f498b5d6a282a"

PR = "r0"

S = "${WORKDIR}"

# MACHINE_EXTRA_RRECOMMENDS += "kernel-module-galcore"

FILES_${PN} += "/lib/modules/${KERNEL_VERSION}/kernel/drivers/mxc/gpu-viv/galcore.ko"
FILES_${PN} += "/etc/modules-load.d/galcore.conf"

do_install_append(){
	echo "galcore" > ${S}/galcore.conf
	install -d ${D}/etc/modules-load.d/
	install -m 0644 ${S}/galcore.conf ${D}/etc/modules-load.d/

	install -d ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/mxc/gpu-viv
	install -m 0644 ${S}/build/sdk/drivers/galcore.ko ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/mxc/gpu-viv/
}

COMPATIBLE_MACHINE = "(s32v234evb)|(s32v234pcie)|(s32v234bbmini)|(s32v234evb28899)"
