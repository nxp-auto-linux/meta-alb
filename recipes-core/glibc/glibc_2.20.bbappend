do_install_prepend() {
	set -x
	# create dummy files/folder needed in the other recipes

	if ! [ -f ${D}/usr/include/bits/fp-fast.h ]; then
		mkdir -p ${D}/usr/include/bits/
		touch ${D}/usr/include/bits/fp-fast.h
	fi

	if ! [ -f ${D}/usr/include/bits/pthreadtypes-arch.h ]; then
		mkdir -p ${D}/usr/include/bits/
		touch ${D}/usr/include/bits/pthreadtypes-arch.h
	fi

	if ! [ -f ${D}/usr/include/gnu/lib-names.h ]; then
		mkdir -p ${D}/usr/include/gnu/
		touch ${D}/usr/include/gnu/lib-names.h
	fi

}

RDEPENDS_nscd += "bash"
