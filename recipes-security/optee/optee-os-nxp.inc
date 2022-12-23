require optee-nxp.inc

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRCREV = "527edd27e02bc288e064f1aa746fff90089aea99"

URL ?= "git://github.com/nxp-auto-linux/optee_os;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${MAJ_VER}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
"

PLATFORM_FLAVOR:s32g2 = "s32g2"
PLATFORM_FLAVOR:s32g3 = "s32g3"
PLATFORM_FLAVOR:s32r45evb = "s32r"

EXTRA_OEMAKE += " \
                PLATFORM_FLAVOR=${PLATFORM_FLAVOR} \
                "
