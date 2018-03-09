
# sysklogd generates a conflict with other packages e.g. inetutils,
# because it installs the binary /sbin/syslogd which is not a link,
# as expected by update-alternatives
do_install_append() {
	if [ -f "${D}${base_sbindir}/syslogd" ]; then
		mv "${D}${base_sbindir}/syslogd" "${D}${base_sbindir}/sysklogd"
	fi
}
