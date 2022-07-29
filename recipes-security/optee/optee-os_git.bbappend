PV = "3.11.0"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

URL ?= "git://source.codeaurora.org/external/autobsps32/optee_os;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${MAJ_VER}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
    file://0001-allow-setting-sysroot-for-libgcc-lookup.patch \
"

SRCREV = "5d081fdbe093385382ced89715449b5ecfb99dfb"

DEPENDS += "python3-pycryptodomex-native dtc-native"

PLATFORM_FLAVOR_s32g2 = "s32g2"
PLATFORM_FLAVOR_s32g3 = "s32g3"
PLATFORM_FLAVOR_s32r45evb = "s32r"

XEN_ARGS = " \
                CFG_VIRTUALIZATION=y \
                "

EXTRA_OEMAKE += " \
                PLATFORM_FLAVOR=${PLATFORM_FLAVOR} \
                ${@bb.utils.contains('DISTRO_FEATURES', 'xen', '${XEN_ARGS}', '', d)} \
                "

OPTEEMACHINE_gen1 = "s32"
OPTEEOUTPUTMACHINE_gen1 = "s32"