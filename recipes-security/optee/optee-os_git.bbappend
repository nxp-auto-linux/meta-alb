PV = "3.11.0"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

URL ?= "git://source.codeaurora.org/external/autobsps32/optee_os;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${MAJ_VER}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
    file://0001-allow-setting-sysroot-for-libgcc-lookup.patch \
"

SRCREV = "4b7ce477bc8cc10a8f1e9baa59cde9b6609cb323"

DEPENDS += "python3-pycryptodomex-native"

OPTEEMACHINE_s32g2 = "s32g"
OPTEEOUTPUTMACHINE_s32g2 = "s32g"
