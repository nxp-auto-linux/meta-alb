# Copyright (C) 2012-2015 Freescale Semiconductor
# Copyright (C) 2012-2013 O.S. Systems Software LTDA.
# Released under the MIT license (see COPYING.MIT for the terms)
# Copyright 2017 NXP

DESCRIPTION = "GPU driver and apps for s32v2xx"
SECTION = "libs"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/gc_vdk.h;endline=26;md5=e4ea72ff91665efb91435921a29148b7"

# DEPENDS += "${@base_contains('DISTRO_FEATURES', 'x11', 'virtual/libx11 libxdamage libxext libxfixes mesa', '', d)}"

DEPENDS += "kernel-module-galcore"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

PE = "1"

inherit fsl-eula-unpack
SRC_URI = "${FSL_LOCAL_MIRROR}/gpu_drivers/Vivante_userspace_libraries_and_demos/gpu-viv-bin-s32v234-6.2.2-hardfp.run;fsl-eula=true \
           file://egl.pc \
           file://glesv2.pc \
           file://vg.pc \
           file://gpu_set_multi_buffer.sh \
          "

# USE_X11 = "${@base_contains("DISTRO_FEATURES", "x11", "yes", "no", d)}"
# USE_DFB = "${@base_contains("DISTRO_FEATURES", "directfb", "yes", "no", d)}"
# USE_WL = "${@base_contains("DISTRO_FEATURES", "wayland", "yes", "no", d)}"

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
}

S = "${WORKDIR}/gpu-viv-bin-s32v234-6.2.2-hardfp"

FILES_${PN} += "/opt"
FILES_${PN} += "/usr"
FILES_${PN} += "/etc"

INSANE_SKIP_${PN} += "dev-deps"
INSANE_SKIP_${PN} += "dev-so"
INSANE_SKIP_${PN} += "ldflags"
INSANE_SKIP_${PN}-dev += "ldflags"

COMPATIBLE_MACHINE = "(s32v234evb)|(s32v234pcie)|(s32v234bbmini)|(s32v234evb28899)"

########################################################################
# Fetcher source clearly states that an unset localpath means that it  #
# is a local file per basepath. We need this to properly support       #
# file:// specs                                                        #
# Add line to get localpath base on url.basepath                       #
########################################################################
python fsl_bin_do_unpack() {
    src_uri = (d.getVar('SRC_URI', True) or "").split()

    if len(src_uri) == 0:
        return

    localdata = bb.data.createCopy(d)
    bb.data.update_data(localdata)

    rootdir = localdata.getVar('WORKDIR', True)
    fetcher = bb.fetch2.Fetch(src_uri, localdata)

    for url in fetcher.ud.values():
        save_cwd = os.getcwd()
        # Check for supported fetchers
        if url.type in ['http', 'https', 'ftp', 'file']:
            if url.parm.get('fsl-eula', False):
                if not url.localpath:
                    url.localpath = url.basepath
                # If download has failed, do nothing
                if not os.path.exists(url.localpath):
                    bb.debug(1, "Exiting as '%s' cannot be found" % url.basename)
                    return

                # Change to the working directory
                bb.note("Handling file '%s' as a Freescale's EULA binary." % url.basename)
                save_cwd = os.getcwd()
                os.chdir(rootdir)

                cmd = "sh %s --auto-accept --force" % (url.localpath)
                bb.fetch2.runfetchcmd(cmd, d, quiet=True)

    # Return to the previous directory
    os.chdir(save_cwd)
}

