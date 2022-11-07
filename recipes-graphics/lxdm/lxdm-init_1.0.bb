SUMMARY = "Simple lxdm Init Script"
SECTION = "x11"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://lxdm \
    file://lxdm.conf \
    file://lxdm.service \
"
S = "${WORKDIR}"

inherit allarch update-rc.d systemd

INITSCRIPT_NAME = "lxdm"
INITSCRIPT_PARAMS = "start 80 5 . stop 80 0 1 2 3 6 ."
INITSCRIPT_PARAMS:shr = "start 90 5 . stop 90 0 1 2 3 6 ."

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install lxdm ${D}${sysconfdir}/init.d

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${sysconfdir}/default
        install -d ${D}${systemd_unitdir}/system
        install lxdm.conf ${D}${sysconfdir}/default/lxdm
        install -m 0644 ${WORKDIR}/lxdm.service ${D}${systemd_unitdir}/system
    fi
}

RDEPENDS:${PN} = "lxdm"

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"
SYSTEMD_SERVICE:${PN} = "lxdm.service"

FILES:${PN} += "${sysconfdir}/default/lxdm"

