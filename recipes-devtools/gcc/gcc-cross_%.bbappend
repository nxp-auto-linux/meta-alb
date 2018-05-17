#
# We want fortran support in the native toolchain to enable numeric algorithm development
#
FORTRAN_append = ",fortran"

EXTRA_OECONF_append = " --with-isl=${STAGING_DIR_NATIVE}${prefix_native} \
"
