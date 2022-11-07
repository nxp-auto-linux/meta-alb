include gcc-${PV}-fsl.inc

#
# We want fortran support in the native toolchain to enable numeric algorithm development
#
FORTRAN:append = ",fortran"

#
# We had libgfortran.a on meta-bluebox
#
DISABLE_STATIC = ""

#
# Enable building libbacktrace, required by libgfortran in gcc 6.x and 7.x
#
do_configure () {
	for target in libbacktrace libgfortran
	do
		rm -rf ${B}/${TARGET_SYS}/$target/
		mkdir -p ${B}/${TARGET_SYS}/$target/
		cd ${B}/${TARGET_SYS}/$target/
		chmod a+x ${S}/$target/configure
		relpath=${@os.path.relpath("${S}", "${B}/${TARGET_SYS}")}
		../$relpath/$target/configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
		# Easiest way to stop bad RPATHs getting into the library since we have a
		# broken libtool here
		sed -i -e 's/hardcode_into_libs=yes/hardcode_into_libs=no/' ${B}/${TARGET_SYS}/$target/libtool
	done
}

do_compile () {
	for target in libbacktrace libgfortran
	do
		cd ${B}/${TARGET_SYS}/$target/
		oe_runmake MULTIBUILDTOP=${B}/${TARGET_SYS}/$target/
	done
}
