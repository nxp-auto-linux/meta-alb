EXTRA_OECONF += '-DDEVRANDOM="\\\"\"/dev/urandom\\\"\"" --with-rand-seed=devrandom'

do_install:append () {
	# fix af alg name
	mv ${D}${libdir}/engines-3/afalg.so ${D}${libdir}/engines-3/af_alg.so

	# do_package_qa requires symlink for .so [dev-elf]
	ln -sf ${@oe.path.relative('${libdir}', '${libdir}/engines-3/af_alg.so')} ${D}${libdir}/libaf_alg.so
}
