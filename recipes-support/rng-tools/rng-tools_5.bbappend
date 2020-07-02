FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"
SRC_URI += " \
	file://default_urandom \
"

do_install_append() {
	if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
		install -m 0644 ${WORKDIR}/default_urandom ${D}${sysconfdir}/default/rng-tools
	fi
}
