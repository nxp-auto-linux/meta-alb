require gcc-${PV}-fsl.inc

DEPENDS_append = "${@bb.utils.contains("DISTRO_FEATURES", "gcc-loop-optimization", " isl", "", d)}"
