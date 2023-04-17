FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

#-----------------------------------------------------------------------
SRC_URI:append:ls2084abluebox = " \
    file://bluebox.conf \
"

do_install:append:ls2084abluebox() {
    # Install sensors configuration file
    install -m 0644 ${WORKDIR}/bluebox.conf ${D}${sysconfdir}/sensors.d
}

#-----------------------------------------------------------------------
SRC_URI:append:ls2084abbmini = " \
    file://ls2084abbmini.conf \
"

do_install:append:ls2084abbmini() {
    # Install sensors configuration file
    install -m 0644 ${WORKDIR}/ls2084abbmini.conf ${D}${sysconfdir}/sensors.d
}
