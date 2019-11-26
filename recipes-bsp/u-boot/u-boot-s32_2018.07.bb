require recipes-bsp/u-boot/u-boot.inc

inherit fsl-u-boot-localversion

DESCRIPTION = "U-boot provided by NXP with focus on S32 chipsets"
PROVIDES = "virtual/bootloader u-boot"

LICENSE = "GPLv2 & BSD-3-Clause & BSD-2-Clause & LGPL-2.0 & LGPL-2.1"
LIC_FILES_CHKSUM = " \
    file://Licenses/gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://Licenses/bsd-2-clause.txt;md5=6a31f076f5773aabd8ff86191ad6fdd5 \
    file://Licenses/bsd-3-clause.txt;md5=4a1190eac56a9db675d58ebe86eaf50c \
    file://Licenses/lgpl-2.0.txt;md5=5f30f0716dfdd0d91eb439ebec522ec2 \
    file://Licenses/lgpl-2.1.txt;md5=4fbd65380cdd255951079008b364516c \
"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "libgcc virtual/${TARGET_PREFIX}gcc dtc-native bc-native"

SRC_URI = "git://source.codeaurora.org/external/autobsps32/u-boot;protocol=https;branch=alb/master"

SRCREV = "40d1a264a0279b70acd0a4aef8abae182c6d0308"

SCMVERSION = "y"
LOCALVERSION = ""

# Support for generating default environment
SRC_URI += " \
    file://0001-env-Add-Makefile-rule-to-generate-default-environment-2018.patch \
"

# Updates required for PFE
DELTA_UBOOT_DEFCONFIG_s32g274aevb = " ${@bb.utils.contains('DISTRO_FEATURES', 'pfe', ' pfe_config.cfg', '', d)}"
SRC_URI_append_s32g274aevb = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pfe', ' file://build/pfe_config.cfg', '', d)} \
"

# Enable Xen default boot if Xen enabled
DELTA_UBOOT_DEFCONFIG_append += "${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'xen_config.cfg', '', d)}"
SRC_URI += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'file://build/xen_config.cfg', '', d)} \
"

# Enable Arm Trusted Firmware
DELTA_UBOOT_DEFCONFIG_append += "${@bb.utils.contains('DISTRO_FEATURES', 'atf', 'atf_config.cfg', '', d)}"
SRC_URI += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'atf', 'file://build/atf_config.cfg', '', d)} \
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
        cp .config ${S}/configs/${config}
    done
}
addtask merge_delta_config before do_configure after do_patch



do_compile_append() {
    if [ ${SOC_FAMILY} = "s32:s32v:s32v2xx" ]; then
        unset i j k
        for config in ${UBOOT_MACHINE}; do
            i=`expr $i + 1`;
            for type in ${UBOOT_CONFIG}; do
                j=`expr $j + 1`;
                for binary in ${UBOOT_BINARIES}; do
                    k=`expr $k + 1`
                    if [ $j -eq $i ] && [ $k -eq $i ]; then
                        # Generate an u-boot image which can be flashed and booted via QSPI
                        if [ "qspi" = "${type}" ];then
                            cp -r ${S}/tools/s32v234-qspi/ ${B}/${config}/tools/s32v234-qspi/
                            cd ${B}/${config}/tools/s32v234-qspi/
                            oe_runmake
                            mv -f ${B}/${config}/${binary} ${B}/${config}/${binary}.nospi
                            cp ${B}/${config}/tools/s32v234-qspi/${binary}.qspi ${B}/${config}/${binary}
                            cp ${B}/${config}/${binary} ${B}/${config}/u-boot-${type}.${UBOOT_SUFFIX}
                            cd -
                        fi
                        cp ${config}/${binary} ${config}/u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX}
                    fi
                done
                unset k
            done
            unset j
        done
        unset i
    fi
}

ENV_STAGE_DIR = "${datadir}/env"

do_install_append() {
    unset i j
    mkdir -p ${D}${ENV_STAGE_DIR}
    # we should have one config
    for config in ${UBOOT_MACHINE}; do
        i=`expr $i + 1`;
        unset j
        for type in ${UBOOT_CONFIG}; do
            j=`expr $j + 1`;
            if [ $j -eq $i ]; then
                # remove any empty lines which might break the environment
                sed '/^[[:space:]]*$/d' -i ${B}/${config}/env-default.txt
                # install our environment file to usr/share to have it staged by yocto
                install ${B}/${config}/env-default.txt ${D}${ENV_STAGE_DIR}/u-boot-default-flashenv${type}.txt
            fi
        done
    done
}

FILES_${PN} += "${ENV_STAGE_DIR}"

COMPATIBLE_MACHINE = "s32"
