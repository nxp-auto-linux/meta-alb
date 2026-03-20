# Copyright 2022, 2024, 2026 NXP

require xen-nxp_git.inc

SRC_URI:append = " \
    file://0001-python-pygrub-pass-DISTUTILS-xen-4.19.patch \
    file://0001-libxl_nocpuid-fix-build-error.patch \
    file://0001-tools-libxl-Fix-build-with-NOCPUID-and-json-c.patch \
    file://0001-tests-vpci-drop-explicit-g-use.patch \
"

EXTRA_OECONF:remove = " --with-system-qemu=${bindir}/qemu-system-i386"
EXTRA_OECONF += "--with-system-qemu=${bindir}/qemu-system-aarch64"

RDEPENDS:${PN} += " \
    ${PN}-libxenmanage \
"

PACKAGES += " \
    ${PN}-libxenmanage \
    ${PN}-libxenmanage-dev \
"

FILES:${PN}-scripts-common += " ${sysconfdir}/xen/*.cfg"

FILES:${PN}-xl += " \
    ${libdir}/xen/bin/init-dom0less \
    ${libdir}/xen/tests/test-xenstore \
    ${libdir}/xen/tests/test-rangeset \
    ${libdir}/xen/tests/test-resource \
    ${libdir}/xen/tests/test-domid \
    ${libdir}/xen/tests/test-paging-mempool \
    ${libdir}/xen/tests/test_vpci \
    ${libdir}/xen/tests/test-pdx-offset \
    ${libdir}/xen/tests/test-pdx-mask \
"

FILES:${PN}-staticdev += " \
    ${libdir}/libxenmanage.a \
"

FILES:${PN}-test += " \
    ${libdir}/xen/bin/test-paging-mempool \
"

FILES:${PN}-xen-watchdog += " \
    ${systemd_unitdir}/system-sleep/xen-watchdog-sleep.sh \
"

FILES:${PN}-xencommons += " \
    ${localstatedir} \
"

FILES:${PN}-libxenmanage = "${libdir}/libxenmanage.so.*"
FILES:${PN}-libxenmanage-dev = " \
    ${libdir}/libxenmanage.so \
    ${libdir}/pkgconfig/xenmanage.pc \
    ${datadir}/pkgconfig/xenmanage.pc \
"

SYSTEMD_SERVICE:${PN}-xencommons:remove = " \
    var-lib-xenstored.mount \
"

# libxenmanage is only in xen-4.21+
ALLOW_EMPTY:${PN}-libxenmanage = "1"

do_install:append() {
    if [ -e ${D}${systemd_unitdir}/system/xen-qemu-dom0-disk-backend.service ]; then
        sed -i 's#ExecStart=.*qemu-system-aarch64\(.*\)$#ExecStart=/usr/bin/qemu-system-aarch64\1#' \
            ${D}${systemd_unitdir}/system/xen-qemu-dom0-disk-backend.service
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'false', 'true', d)}; then
        sed -i 's#\(test -z "$QEMU_XEN" && QEMU_XEN=\).*$#\1"/usr/bin/qemu-system-aarch64"#' ${D}/etc/init.d/xencommons
    fi
}
