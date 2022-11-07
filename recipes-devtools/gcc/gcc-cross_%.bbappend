EXTRA_INCLUDE_FILE = "gcc-cross-fsl.inc"

include gcc-${PV}-fsl.inc

#
# We want fortran support in the native toolchain to enable numeric algorithm development
#
FORTRAN:append = ",fortran"
