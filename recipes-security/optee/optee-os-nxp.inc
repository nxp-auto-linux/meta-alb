require optee-nxp.inc

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRCREV = "3bddb520deab1d9a2a15bfcd57b89fa2f6be5de6"

URL ?= "git://github.com/nxp-auto-linux/optee_os;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${MAJ_VER}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
"

DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'hse', 'hse-firmware', '', d)}"

PLATFORM_FLAVOR:s32g2 = "s32g2"
PLATFORM_FLAVOR:s32g3 = "s32g3"
PLATFORM_FLAVOR:s32r45evb = "s32r"

HSE_ARGS = " \
              CFG_CRYPTO_DRIVER=y \
              HSE_FWDIR=${STAGING_INCDIR}/hse-interface \
	       "

EXTRA_OEMAKE += " \
                PLATFORM_FLAVOR=${PLATFORM_FLAVOR} \
                "
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'hse', '${HSE_ARGS}', '', d)}"
