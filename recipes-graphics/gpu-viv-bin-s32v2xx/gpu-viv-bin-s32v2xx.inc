# Copyright (C) 2012-2015 Freescale Semiconductor
# Copyright (C) 2012-2013 O.S. Systems Software LTDA.
# Released under the MIT license (see COPYING.MIT for the terms)
# Copyright 2017-2019 NXP

DESCRIPTION = "GPU driver and apps for s32v2xx"
SECTION = "libs"
LICENSE = "Proprietary"

DEPENDS += "kernel-module-galcore"
RDEPENDS_${PN} += "kernel-module-galcore"

PROVIDES += "virtual/wayland-egl virtual/libgal-x11 virtual/libopenvg"
PROVIDES += "virtual/opencl-icd opencl-headers"
PROVIDES += "virtual/libgles1 virtual/libgles2 virtual/egl"
RPROVIDES_${PN} = "gpu-viv-bin-s32v2xx"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

PE = "1"

# With this declaration and the updated fsl-eula-unpack class, we can detect license file
# changes properly on a per-recipe basis
FSL_EULA_CHKSUM = "ab61cab9599935bfe9f700405ef00f28"

inherit fsl-auto-eula-unpack pkgconfig

# We support two kernel versions; 4.19 (latest, default) and 4.14.
# The following code enables support for using a different version
# of the binaries for different version of kernel.
KERNEL_NAME = "${PREFERRED_PROVIDER_virtual/kernel}"
KERNEL_VER = '${@d.getVar("PREFERRED_VERSION_${KERNEL_NAME}",True)}'
PV = "${@oe.utils.conditional("KERNEL_VER", "5.4", "6.4.0", "6.2.4", d)}"

# We may have a version suffix for various path elements
PVS = "${@oe.utils.conditional("PV", "6.4.0", ".p2", ".p4", d)}"
LIC_FILES_MD5 = "${@oe.utils.conditional("PV", "6.4.0", \
	"f01fe31fae02a620fd468b2e68e7222b", "f5411d7aeec487c2c1ffcc664e866832", d)}"
LIC_FILES_CHKSUM = "file://usr/include/gc_vdk.h;endline=26;md5=${LIC_FILES_MD5}"


S = "${WORKDIR}/gpu-viv-bin-s32v234-${PV}${PVS}-hardfp"

# Dummy entry to keep the recipe parser happy if we don't use this recipe
FSL_LOCAL_MIRROR ?= "file://."

SRC_URI = "${FSL_LOCAL_MIRROR}/Vivante_userspace_libraries_and_demos/gpu-viv-bin-s32v234-${PV}${PVS}-hardfp.run;fsl-eula=true \
           file://egl.pc \
           file://glesv2.pc \
           file://vg.pc \
           file://gpu_set_multi_buffer.sh \
           file://gpu_disable_prefetch.sh \
          "

do_install_append () {
    install -d ${D}/usr
    install -d ${D}/opt

    cp -axr ${S}/usr ${D}
    cp -axr ${S}/opt ${D}

    rm -rf ${D}/${includedir}/KHR

    mkdir -p ${D}${libdir}/pkgconfig
    install -m 0644 ${WORKDIR}/egl.pc ${D}${libdir}/pkgconfig/egl.pc
    install -m 0644 ${WORKDIR}/glesv2.pc ${D}${libdir}/pkgconfig/glesv2.pc
    install -m 0644 ${WORKDIR}/vg.pc ${D}${libdir}/pkgconfig/vg.pc

    install -d ${D}/etc
    cp -axr ${S}/etc ${D}

    install -d ${D}${sysconfdir}/profile.d/
    install -m 0755 ${WORKDIR}/gpu_set_multi_buffer.sh ${D}${sysconfdir}/profile.d/
    install -m 0755 ${WORKDIR}/gpu_disable_prefetch.sh ${D}${sysconfdir}/profile.d/
    
    # We need this line to avoid warnings:
    # '<file> is owned by uid 1000, which is the same as the user running bitbake.
    # This may be due to host contamination [host-user-contaminated]'
    chown -R root:root ${D}/opt ${D}/usr/lib ${D}/etc
}

# **** HACK START ****
# Here I have to fix a Vivante weirdness in shared library creation
# that needlessly complicates package separation. The Vivante libs do
# not properly distinguish between linker name and fully qualified
# library name, so a clear separation between main library and dev
# package content is not possible unless we do a hard movement of files
# between packages.
# We do this by changing the wildcard definition of which libs go where.
STRANGELIBS = " \
        ${libdir}/libCLC.so \
        ${libdir}/libGAL.fb.so \
        ${libdir}/libGAL.so \
        ${libdir}/libGLSLC.so \
        ${libdir}/libOpenCL.so \
        ${libdir}/libOpenVG.2d.so \
        ${libdir}/libOpenVG.3d.so \
        ${libdir}/libOpenVG.so \
        ${libdir}/libVDK.so \
        ${libdir}/libVIVANTE.fb.so \
        ${libdir}/libVivanteOpenCL.so \
        ${libdir}/libVIVANTE.so \
        ${libdir}/libVSC.so \
"        
DEVLIBS = " \
        ${libdir}/libEGL.so \
        ${libdir}/libGELS_CL.so \
        ${libdir}/libGLES_CM.so \
        ${libdir}/libGLESv1_CL.so \
        ${libdir}/libGLESv2_CM.so \
        ${libdir}/libGLESv2.so \
"
FILES_SOLIBSDEV="${base_libdir}/lib*.so"
FILES_${PN} += "${STRANGELIBS}"
FILES_${PN} += "${DEVLIBS}"
# **** HACK END ****

PACKAGES += "${PN}-samples"

FILES_${PN} += "/opt/fsl_tools"
FILES_${PN} += "/usr"
FILES_${PN} += "/etc"
FILES_${PN}-samples += "/opt/viv_samples"

INSANE_SKIP_${PN} += "dev-deps"
INSANE_SKIP_${PN} += "dev-so"
INSANE_SKIP_${PN} += "ldflags"
INSANE_SKIP_${PN}-dev += "ldflags"
INSANE_SKIP_${PN}-samples += "ldflags"
INSANE_SKIP_${PN} += "already-stripped"

COMPATIBLE_MACHINE = "(s32v234evb)|(s32v234pcie)|(s32v234bbmini)|(s32v234evb28899)|(s32v234sbc)"

