require optee-nxp.inc

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRCREV = "ec48d162ea07f8ead18b4b850364d5724606cd31"

URL ?= "git://github.com/nxp-auto-linux/optee_test.git;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${MAJ_VER}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
    file://run-ptest \
"
