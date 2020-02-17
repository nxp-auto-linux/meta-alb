# rsh and rshd were disabled from original recipe, as bad for security
# re-enable them here
EXTRA_OECONF_append = " --enable-rsh --enable-rshd "
EXTRA_OECONF_remove = "--disable-rsh --disable-rshd"

do_install_append () {
    cp ${WORKDIR}/rexec.xinetd.inetutils  ${D}/${sysconfdir}/xinetd.d/rexec
    cp ${WORKDIR}/rlogin.xinetd.inetutils  ${D}/${sysconfdir}/xinetd.d/rlogin
    cp ${WORKDIR}/rsh.xinetd.inetutils  ${D}/${sysconfdir}/xinetd.d/rsh

    sed -e 's,@SBINDIR@,${sbindir},g' -i ${D}/${sysconfdir}/xinetd.d/*
}
