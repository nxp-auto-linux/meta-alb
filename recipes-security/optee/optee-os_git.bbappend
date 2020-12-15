PV = "3.9.0"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

URL ?= "git://source.codeaurora.org/external/autobsps32/optee_os;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${MAJ_VER}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
    file://0001-allow-setting-sysroot-for-libgcc-lookup.patch \
"

SRCREV = "b931c366a69c5e38a058b5234c61772cc85aae49"

DEPENDS += "python3-pycryptodomex-native"

OPTEEMACHINE_s32g2 = "s32g"
OPTEEOUTPUTMACHINE_s32g2 = "s32g"
