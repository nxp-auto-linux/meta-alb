require optee-nxp.inc

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRCREV = "5bc04d55cc67a75824cad7a8ee4b29090ee11187"

URL ?= "git://github.com/nxp-auto-linux/optee_os;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${MAJ_VER}"
SRC_URI = "\
    ${URL};branch=${BRANCH} \
    file://0001-allow-setting-sysroot-for-libgcc-lookup.patch \
    file://0002-core-Define-section-attributes-for-clang.patch \
    file://0003-optee-enable-clang-support.patch \
    file://0004-core-link-add-no-warn-rwx-segments.patch \
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

# Switch the UART baudrate to be used if it is the case
EXTRA_OEMAKE += "${@ 'CFG_S32_UART_BAUDRATE=' + d.getVar("SERIAL_CONSOLES", True).split(" ")[0].split(";")[0] }"
