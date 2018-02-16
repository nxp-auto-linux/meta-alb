# Copyright (C) 2012-2015 Freescale Semiconductor
# Copyright (C) 2012-2013 O.S. Systems Software LTDA.
# Released under the MIT license (see COPYING.MIT for the terms)
# Copyright 2017-2018 NXP

DESCRIPTION = "GPU driver and apps for s32v2xx"
SECTION = "libs"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/gc_vdk.h;endline=26;md5=e4ea72ff91665efb91435921a29148b7"

DEPENDS += "kernel-module-galcore"

PROVIDES = "gpu-viv-bin-s32v2xx"
RPROVIDES_${PN} = "gpu-viv-bin-s32v2xx"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

PE = "1"

# With this declaration and the updated fsl-eula-unpack class, we can detect license file
# changes properly on a per-recipe basis
FSL_EULA_CHKSUM = "c9ae442cf1f9dd6c13dfad64b0ffe73f"

inherit fsl-auto-eula-unpack pkgconfig

S = "${WORKDIR}/gpu-viv-bin-s32v234-6.2.2-hardfp"

# Dummy entry to keep the recipe parser happy if we don't use this recipe
FSL_LOCAL_MIRROR ?= "file://."

SRC_URI = "${FSL_LOCAL_MIRROR}/gpu_drivers/Vivante_userspace_libraries_and_demos/gpu-viv-bin-s32v234-6.2.2-hardfp.run;fsl-eula=true \
           file://egl.pc \
           file://glesv2.pc \
           file://vg.pc \
           file://gpu_set_multi_buffer.sh \
          "

do_install_append () {
    install -d ${D}/usr
    install -d ${D}/opt

    cp -axr ${S}/usr ${D}
    cp -axr ${S}/opt ${D}

    mkdir -p ${D}${libdir}/pkgconfig
    install -m 0644 ${WORKDIR}/egl.pc ${D}${libdir}/pkgconfig/egl.pc
    install -m 0644 ${WORKDIR}/glesv2.pc ${D}${libdir}/pkgconfig/glesv2.pc
    install -m 0644 ${WORKDIR}/vg.pc ${D}${libdir}/pkgconfig/vg.pc

    find ${D}/usr -type f -exec chmod 644 {} \;
    find ${D}/opt -type f -exec chmod 644 {} \;

    install -d ${D}/etc
    cp -axr ${S}/etc ${D}
    find ${D}/etc -type f -exec chmod 644 {} \;

    install -d ${D}${sysconfdir}/profile.d/
    install -m 0755 ${WORKDIR}/gpu_set_multi_buffer.sh ${D}${sysconfdir}/profile.d/
    
    # We need this line to avoid warnings:
    # '<file> is owned by uid 1000, which is the same as the user running bitbake.
    # This may be due to host contamination [host-user-contaminated]'
    chown -R root:root ${D}/opt ${D}/usr/lib ${D}/etc
}

S = "${WORKDIR}/gpu-viv-bin-s32v234-6.2.2-hardfp"

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
FILES_SOLIBSDEV="${base_libdir}/lib*.so ${DEVLIBS}"
FILES_${PN} += "${STRANGELIBS}"
# **** HACK END ****

FILES_${PN} += "/opt"
FILES_${PN} += "/usr"
FILES_${PN} += "/etc"

INSANE_SKIP_${PN} += "dev-deps"
INSANE_SKIP_${PN} += "dev-so"
INSANE_SKIP_${PN} += "ldflags"
INSANE_SKIP_${PN}-dev += "ldflags"

COMPATIBLE_MACHINE = "(s32v234evb)|(s32v234pcie)|(s32v234bbmini)|(s32v234evb28899)"

