DEPENDS:append = "\
	libnet \
	libcli \
"

do_compile() {
	oe_runmake allbutcurvetun
}

do_install() {
	oe_runmake DESTDIR=${D} install_allbutcurvetun
}
