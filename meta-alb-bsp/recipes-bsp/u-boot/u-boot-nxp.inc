require recipes-bsp/u-boot/u-boot.inc
require recipes-bsp/u-boot/u-boot-quick-boot.inc

inherit fsl-u-boot-localversion

SUMMARY = "U-boot provided by NXP"
LICENSE = "GPL-2.0-only & BSD-3-Clause & BSD-2-Clause & LGPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://Licenses/gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://Licenses/bsd-2-clause.txt;md5=6a31f076f5773aabd8ff86191ad6fdd5 \
    file://Licenses/bsd-3-clause.txt;md5=4a1190eac56a9db675d58ebe86eaf50c \
    file://Licenses/lgpl-2.1.txt;md5=4fbd65380cdd255951079008b364516c \
"

DEPENDS:append = " libgcc dtc-native bison-native bc-native"
DEPENDS:append:qoriq-ppc = " boot-format-native"

PROVIDES = "virtual/bootloader u-boot"

SCMVERSION = "y"
LOCALVERSION = ""

HSE_SUPPORT = ""
HSE_SUPPORT:s32cc = "${@bb.utils.contains('DISTRO_FEATURES', 'hse', 'recipes-bsp/u-boot/u-boot-nxp-hse.inc', '', d)}"
include ${HSE_SUPPORT}

VERIFIEDBOOT_SUPPORT = ""
VERIFIEDBOOT_SUPPORT:s32cc = "${@bb.utils.contains('DISTRO_FEATURES', 'verifiedboot', 'recipes-bsp/u-boot/u-boot-nxp-verifiedboot.inc', '', d)}"
include ${VERIFIEDBOOT_SUPPORT}

DELTA_UBOOT_DEFCONFIG ?= ""

# Enable Xen default boot if Xen enabled
DELTA_UBOOT_DEFCONFIG:append:s32 = " ${@bb.utils.contains('DISTRO_FEATURES', 'xen', ' xen_config.cfg', '', d)}"
SRC_URI:append:s32 = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'file://build/xen_config.cfg', '', d)} \
"

# Enable OP-TEE
DELTA_UBOOT_DEFCONFIG:append:s32cc = "${@bb.utils.contains('DISTRO_FEATURES', 'optee', ' optee_config.cfg', '', d)}"
SRC_URI:append:s32cc = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'file://build/optee_config.cfg', '', d)} \
"

# Switch the UART to be used if it is the case
DELTA_UBOOT_DEFCONFIG:append:s32cc = " ${@ 'serial' + d.getVar("SERIAL_CONSOLES", True).split(" ")[0].split(";")[1][-1] + '.cfg' }"
SRC_URI:append:s32cc = " \
    ${@ 'file://build/serial' + d.getVar("SERIAL_CONSOLES", True).split(" ")[0].split(";")[1][-1] + '.cfg' } \
"

# Switch the UART baudrate to be used if it is the case
S32_BAUDRATE = "${@d.getVar("SERIAL_CONSOLES", True).split(" ")[0].split(";")[0] }"
DELTA_UBOOT_DEFCONFIG:append:s32cc = " serial_configs/baudrate_${S32_BAUDRATE}.cfg"
SRC_URI:append:s32cc = " \
    ${@ 'file://build/serial_configs/baudrate_${S32_BAUDRATE}.cfg' } \
"

# Enable eMMC High Speed Modes (HS200/HS400/HS400ES)
DELTA_UBOOT_DEFCONFIG:append:s32g = "${@bb.utils.contains('EMMC_FEATURES', 'high-speed', ' emmc_hs_enablement.cfg', '', d)}"
SRC_URI:append:s32g = " \
    ${@bb.utils.contains('EMMC_FEATURES', 'high-speed', 'file://build/emmc_hs_enablement.cfg', '', d)} \
"
DELTA_UBOOT_DEFCONFIG:append:s32g274ardb2 = "${@bb.utils.contains('DISTRO_FEATURES', 'pfe-slave', ' pfe-slave_config.cfg', '', d)}"
DELTA_UBOOT_DEFCONFIG:remove:s32g274ardb2 = "${@bb.utils.contains('DISTRO_FEATURES', 'pfe', ' pfe-slave_config.cfg', '', d)}"
SRC_URI:append:s32g274ardb2 = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pfe-slave', 'file://build/pfe-slave_config.cfg', '', d)} \
"

DELTA_UBOOT_DEFCONFIG:append:s32g399ardb3 = "${@bb.utils.contains('DISTRO_FEATURES', 'pfe-slave', ' pfe-slave_config.cfg', '', d)}"
DELTA_UBOOT_DEFCONFIG:remove:s32g399ardb3 = "${@bb.utils.contains('DISTRO_FEATURES', 'pfe', ' pfe-slave_config.cfg', '', d)}"
SRC_URI:append:s32g399ardb3 = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pfe-slave', 'file://build/pfe-slave_config.cfg', '', d)} \
"

# FIXME: Allow linking of 'tools' binaries with native libraries
#        used for generating the boot logo and other tools used
#        during the build process.
EXTRA_OEMAKE += 'HOSTCC="${BUILD_CC} ${BUILD_CPPFLAGS}" \
                 HOSTLDFLAGS="${BUILD_LDFLAGS}" \
                 HOSTSTRIP=true'

PACKAGE_ARCH = "${MACHINE_ARCH}"

USRC ?= ""
S = '${@oe.utils.conditional("USRC", "", "${WORKDIR}/git", "${USRC}", d)}'
B = "${WORKDIR}/build"

do_merge_delta_config[dirs] = "${B}"
do_merge_delta_config() {
    # add config fragments
    echo ${UBOOT_MACHINE}
    for config in ${UBOOT_MACHINE}; do
        # replace <config-type>_config to <config-type>_defconfig to
        # match the config name file
        config="$(echo "${config}" | sed -e 's/'_config'/'_defconfig'/g')"

        cp ${S}/configs/${config} .config
        for deltacfg in ${DELTA_UBOOT_DEFCONFIG}; do
            ${S}/scripts/kconfig/merge_config.sh -m .config ${deltacfg}
        done
        cp -vf ${B}/.config ${S}/configs/${config}
    done
}
addtask merge_delta_config before do_configure after do_patch

ENV_STAGE_DIR = "${datadir}/env"

do_install:append:s32() {
    mkdir -p ${D}${ENV_STAGE_DIR}

    if [ -z "${UBOOT_CONFIG}" ]; then
        # remove any empty lines which might break the environment
        sed '/^[[:space:]]*$/d' -i ${B}/env-default.txt
        # install our environment file to usr/share to have it staged by yocto
        install ${B}/env-default.txt ${D}${ENV_STAGE_DIR}/u-boot-default-flashenv.txt
    else
        # we should have one config
        unset i
        for config in ${UBOOT_MACHINE}; do
            i=`expr $i + 1`;
            unset j
            for type in ${UBOOT_CONFIG}; do
                j=`expr $j + 1`;
                if [ $j -ne $i ]; then
                    continue
                fi

                # remove any empty lines which might break the environment
                sed '/^[[:space:]]*$/d' -i ${B}/${config}/env-default.txt
                # install our environment file to usr/share to have it staged by yocto
                install ${B}/${config}/env-default.txt ${D}${ENV_STAGE_DIR}/u-boot-default-flashenv${type}.txt
            done
        done
    fi
}

do_deploy:append:s32() {
    install -d ${DEPLOYDIR}
    install -d ${DEPLOYDIR}/tools

    if [ -z "${UBOOT_CONFIG}" ]; then
        install -v ${B}/u-boot-nodtb.bin ${DEPLOYDIR}/u-boot-nodtb.bin
    else
        unset i
        for config in ${UBOOT_MACHINE}; do
            i=`expr $i + 1`;
            unset j
            for type in ${UBOOT_CONFIG}; do
                j=`expr $j + 1`;
                if [ $j -ne $i ]; then
                    continue
                fi

                install -v ${B}/${config}/u-boot-nodtb.bin ${DEPLOYDIR}/u-boot-nodtb.bin-${type}
            done
        done
    fi
}

do_deploy:append:s32cc() {
    unset i
    for config in ${UBOOT_MACHINE}; do
        i=`expr $i + 1`;
        unset j
        for type in ${UBOOT_CONFIG}; do
            j=`expr $j + 1`;
            if [ $j -ne $i ]; then
                continue
            fi


            install -v ${B}/${config}/u-boot-s32.cfgout ${DEPLOYDIR}/tools/u-boot-s32.cfgout-${type}
            find ${B}/${config}/ -name "qspi_params_*" -exec install -v {} ${DEPLOYDIR}/tools/ \;
        done
    done
}

do_deploy:append:s32n7sim() {
    install -d "${DEPLOYDIR}/simimage/u-boot"

    # Deploy the ELF for symbols
    if [ -z "${UBOOT_CONFIG}" ]; then
        install -v "${B}/u-boot" "${DEPLOYDIR}/simimage/u-boot/u-boot"
    else
        unset i
        for config in ${UBOOT_MACHINE}; do
            i=`expr $i + 1`;
            unset j
            for type in ${UBOOT_CONFIG}; do
                j=`expr $j + 1`;
                if [ $j -ne $i ]; then
                    continue
                fi

                install -v "${B}/${config}/u-boot" "${DEPLOYDIR}/simimage/u-boot/u-boot-${type}"
            done
        done
    fi
}

FILES:${PN} += "${ENV_STAGE_DIR}"

COMPATIBLE_MACHINE = "s32|qoriq"
