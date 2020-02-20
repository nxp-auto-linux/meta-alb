EXTRA_OECONF += '-DDEVRANDOM="\\\"\"/dev/urandom\\\"\"" --with-rand-seed=devrandom'
do_install_append () {
	# fix af alg name
	mv ${D}${libdir}/engines-1.1/afalg.so ${D}${libdir}/engines-1.1/af_alg.so

	# do_package_qa requires symlink for .so [dev-elf]
	ln -sf ${@oe.path.relative('${libdir}', '${libdir}/engines-1.1/af_alg.so')} ${D}${libdir}/libaf_alg.so
}
