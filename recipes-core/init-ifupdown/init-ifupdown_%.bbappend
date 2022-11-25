FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
#
# We really should have board specific interfaces files,
# so that the primary Eth connector comes up for dhcp
# by default.
#
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'file://interfaces', '', d)}"

do_install:ubuntu () {
       install -d ${D}${sysconfdir}/network/interfaces.d
       install -m 0644 ${WORKDIR}/interfaces ${D}${sysconfdir}/network/interfaces.d
}

# If we provide netbase package from Ubuntu, RCONFLICTS
# generates an error, as package version cannot be seen,
# thus we need to overwrite this.

RCONFLICTS:${PN}:ubuntu = ""
