PV = "3.11.0"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

URL ?= "git://source.codeaurora.org/external/autobsps32/optee_os;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${MAJ_VER}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
    file://0001-allow-setting-sysroot-for-libgcc-lookup.patch \
"

SRCREV = "ba222a71df3a521ae88f658ba58e3ad6b4c94661"

DEPENDS += "python3-pycryptodomex-native"

OPTEEMACHINE_s32g2 = "s32g"
OPTEEOUTPUTMACHINE_s32g2 = "s32g"
