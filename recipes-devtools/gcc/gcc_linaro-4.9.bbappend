
do_configure_prepend() {
	# fix meta-linaro-toolchain: gcc_linaro-4.9.bb where it access libcc1/configure
	# even if it does not exist, resulting in configure failure.
	if ! [ -f ${S}/libcc1/configure ]; then
		mkdir -p ${S}/libcc1
		touch ${S}/libcc1/configure
	fi
}
