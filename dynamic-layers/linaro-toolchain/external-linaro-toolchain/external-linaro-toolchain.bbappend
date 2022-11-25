inherit insane
LIC_FILES_CHKSUM = "\
        file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
       "

PROVIDES += "virtual/libc-locale virtual/crypt"

do_install:prepend() {
	# create dummy files/folder needed in the other recipes

	if ! [ -f ${D}/usr/include/bits/long-double.h ]; then
		mkdir -p ${D}/usr/include/bits/
		touch ${D}/usr/include/bits/long-double.h
	fi

	if ! [ -f ${D}/usr/include/bits/fp-fast.h ]; then
		mkdir -p ${D}/usr/include/bits/
		touch ${D}/usr/include/bits/fp-fast.h
	fi

	if ! [ -f ${D}/usr/include/bits/pthreadtypes-arch.h ]; then
		mkdir -p ${D}/usr/include/bits/
		touch ${D}/usr/include/bits/pthreadtypes-arch.h
	fi



}


do_install:append() {
	# Standard external linaro toolchains have no symbolic link for libraries
	# To free-up some space, linaro do_install will remove unused files

	# For Ubuntu toolchain, the host cross toolchain has symlink instead
	# We need to copy back the files that have been deleted in standard
	# do_install

	cp -a ${EXTERNAL_TOOLCHAIN}/${ELT_TARGET_SYS}/libc/usr/lib/lib*-${ELT_VER_LIBC}.so ${D}${base_libdir}/

	cp -a ${EXTERNAL_TOOLCHAIN}/${ELT_TARGET_SYS}/libc/usr/lib/libthread_db.so  ${D}${base_libdir}/
}

INSANE_SKIP:${PN}:ubuntu = "installed-vs-shipped build-deps"
