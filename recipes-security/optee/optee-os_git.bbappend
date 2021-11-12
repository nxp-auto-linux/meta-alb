PV = "3.11.0"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

URL ?= "git://source.codeaurora.org/external/autobsps32/optee_os;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${MAJ_VER}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
    file://0001-allow-setting-sysroot-for-libgcc-lookup.patch \
"

SRCREV = "2c1f6b3fd501b44145a7944a40772001086f9caa"

DEPENDS += "python3-pycryptodomex-native"

PLATFORM_FLAVOR_s32g2 = "s32g2"
PLATFORM_FLAVOR_s32g3 = "s32g3"

EXTRA_OEMAKE += " \
                PLATFORM_FLAVOR=${PLATFORM_FLAVOR} \
                "

OPTEEMACHINE_s32g = "s32g"
OPTEEOUTPUTMACHINE_s32g = "s32g"
