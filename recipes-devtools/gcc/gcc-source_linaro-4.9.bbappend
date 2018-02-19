
#EXCLUDE_FROM_WORLD = "1"

# Fix meta/recipes-devtools/gcc/gcc-source.inc" from poky/rocko
# Here, simply removed the following lines:

#	# Easiest way to stop bad RPATHs getting into the library since we have a   
#	# broken libtool here (breaks cross-canadian and target at least)           
#	cmd = d.expand("sed -i -e 's/hardcode_into_libs=yes/hardcode_into_libs=no/' ${S}/libcc1/configure")
#	subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)          

# For gcc-linaro-4.9, there is no libcc1 folder, making the above lines to triger an error

python do_preconfigure () {
    import subprocess
    cmd = d.expand('PATH=${PATH} cd ${S} && gnu-configize')
    subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
    # See 0044-gengtypes.patch, we need to regenerate this file
    bb.utils.remove(d.expand("${S}/gcc/gengtype-lex.c"))
    cmd = d.expand("sed -i 's/BUILD_INFO=info/BUILD_INFO=/' ${S}/gcc/configure")
    subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
}
