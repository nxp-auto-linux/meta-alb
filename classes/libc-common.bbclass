do_install() {
	oe_runmake install_root=${D} install
	install -Dm 0644 ${WORKDIR}/etc/ld.so.conf ${D}/${sysconfdir}/ld.so.conf
	install -d ${D}${localedir}
	make -f ${WORKDIR}/generate-supported.mk IN="${S}/localedata/SUPPORTED" OUT="${WORKDIR}/SUPPORTED"
	# get rid of some broken files...
	for i in ${GLIBC_BROKEN_LOCALES}; do
		sed -i "/$i/d" ${WORKDIR}/SUPPORTED
	done
	rm -f ${D}${sysconfdir}/rpc
	rm -rf ${D}${datadir}/zoneinfo
	rm -rf ${D}${libexecdir}/getconf
}

def get_libc_fpu_setting(bb, d):
    if d.getVar('TARGET_FPU') in [ 'soft', 'ppc-efd' ]:
        return "--without-fp"
    return ""

python populate_packages:prepend () {
    if d.getVar('DEBIAN_NAMES'):
        pkgs = d.getVar('PACKAGES').split()
        bpn = d.getVar('BPN')
        prefix = d.getVar('MLPREFIX') or ""
        # Set the base package...
        d.setVar('PKG:' + prefix + bpn, prefix + 'libc6')
        libcprefix = prefix + bpn + '-'
        for p in pkgs:
            # And all the subpackages.
            if p.startswith(libcprefix):
                renamed = p.replace(bpn, 'libc6', 1)
                d.setVar('PKG:' + p, renamed)
        # For backward compatibility with old -dbg package
        d.appendVar('RPROVIDES:' + libcprefix + 'dbg', ' ' + prefix + 'libc-dbg')
        d.appendVar('RCONFLICTS:' + libcprefix + 'dbg', ' ' + prefix + 'libc-dbg')
        d.appendVar('RREPLACES:' + libcprefix + 'dbg', ' ' + prefix + 'libc-dbg')
}
