# Copyright 2022 NXP

require xen-nxp_git.inc

FILES:${PN}-scripts-common += " ${sysconfdir}/xen/*.cfg"

FILES:${PN}-xl += " \
    ${libdir}/xen/bin/init-dom0less \
"

FILES:${PN}-test += " \
    ${libdir}/xen/bin/test-paging-mempool \
"

FILES:${PN}-xencommons += " \
    ${localstatedir} \
"

SYSTEMD_SERVICE:${PN}-xencommons:remove = " \
    var-lib-xenstored.mount \
"
