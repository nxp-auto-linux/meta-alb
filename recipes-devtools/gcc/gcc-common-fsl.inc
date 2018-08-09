EXTRA_INCLUDE_FILE ?= ""

EXTRA_INCLUDE = "${@bb.utils.contains("DISTRO_FEATURES", "gcc-loop-optimization", "gcc-isl.inc", "", d)}"
EXTRA_INCLUDE += "${@bb.utils.contains("DISTRO_FEATURES", "gcc-loop-optimization", "${EXTRA_INCLUDE_FILE}", "", d)}"

require ${EXTRA_INCLUDE}
