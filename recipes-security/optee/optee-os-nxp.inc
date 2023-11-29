require optee-nxp.inc

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRCREV = "22e4364181591afb6bfec249f4b7e9412235218d"

URL ?= "git://github.com/nxp-auto-linux/optee_os;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${MAJ_VER}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
"

DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'hse', 'hse-firmware', '', d)}"

PLATFORM_FLAVOR:s32g2 = "s32g2"
PLATFORM_FLAVOR:s32g3 = "s32g3"
PLATFORM_FLAVOR:s32r45 = "s32r"

HSE_ARGS = " \
              CFG_NXP_HSE=y \
              CFG_NXP_HSE_FWDIR=${STAGING_INCDIR}/hse-interface \
	       "
XEN_ARGS = " \
              CFG_VIRTUALIZATION=y \
           "

EXTRA_OEMAKE += " \
                PLATFORM_FLAVOR=${PLATFORM_FLAVOR} \
                "
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'hse', '${HSE_ARGS}', '', d)}"
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'xen', '${XEN_ARGS}', '', d)}"
