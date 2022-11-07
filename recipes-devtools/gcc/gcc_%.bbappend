include gcc-${PV}-fsl.inc

DEPENDS:append = "${@bb.utils.contains("DISTRO_FEATURES", "gcc-loop-optimization", " isl", "", d)}"

#
# We want fortran support in the native toolchain to enable numeric algorithm development
#
FORTRAN:append = ",fortran"