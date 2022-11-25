SUMMARY = "Simple x11vnc Init Script"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
SECTION = "x11"

PR = "r1"

SRC_URI = "\
    file://gplv2-license.patch \
    file://x11vnc \
    file://x11vnc.conf \
    file://x11vnc.service \
"
S = "${WORKDIR}"

inherit allarch update-rc.d systemd

INITSCRIPT_NAME = "x11vnc"
INITSCRIPT_PARAMS = "start 81 5 . stop 79 0 1 2 3 6 ."
INITSCRIPT_PARAMS:shr = "start 91 5 . stop 89 0 1 2 3 6 ."

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install x11vnc ${D}${sysconfdir}/init.d

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${sysconfdir}/default
        install -d ${D}${systemd_unitdir}/system
        install x11vnc.conf ${D}${sysconfdir}/default/x11vnc
        install -m 0644 ${WORKDIR}/x11vnc.service ${D}${systemd_unitdir}/system
    fi
}

RDEPENDS:${PN} = "x11vnc"
# No need to enforce this dependency for Ubuntu
RDEPENDS:${PN}:ubuntu = ""

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"
SYSTEMD_SERVICE:${PN} = "x11vnc.service"

FILES:${PN} += "${sysconfdir}/default/x11vnc"

