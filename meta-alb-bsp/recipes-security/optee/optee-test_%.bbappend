require optee-nxp.inc

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRCREV = "79e49f95474d05a028f58a75c6c643f88e9b9295"

URL ?= "git://github.com/nxp-auto-linux/optee_test.git;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${MAJ_VER}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
    file://run-ptest \
"
