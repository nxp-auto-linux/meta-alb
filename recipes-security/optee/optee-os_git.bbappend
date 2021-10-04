PV = "3.11.0"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

URL ?= "git://source.codeaurora.org/external/autobsps32/optee_os;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${MAJ_VER}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
    file://0001-allow-setting-sysroot-for-libgcc-lookup.patch \
"

SRCREV = "9ab5f81ca5670fa47acab2cb10157630207d1f7a"

DEPENDS += "python3-pycryptodomex-native"

OPTEEMACHINE_s32g2 = "s32g"
OPTEEOUTPUTMACHINE_s32g2 = "s32g"
