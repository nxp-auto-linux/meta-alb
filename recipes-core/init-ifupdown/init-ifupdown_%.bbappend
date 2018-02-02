FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"
#
# We really should have board specific interfaces files,
# so that the primary Eth connector comes up for dhcp
# by default.
#
SRC_URI += "file://interfaces"

do_install_ubuntu () {
       install -d ${D}${sysconfdir}/network/interfaces.d
       install -m 0644 ${WORKDIR}/interfaces ${D}${sysconfdir}/network/interfaces.d
}

# If we provide netbase package from Ubuntu, RCONFLICTS
# generates an error, as package version cannot be seen,
# thus we need to overwrite this.

RCONFLICTS_${PN}_ubuntu = ""
