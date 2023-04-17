# I added this because there is no other way to generate the default
# flash environment for U-Boot properly
# <Heinz.Wrobel@nxp.com>
#
SUMMARY = "Create U-Boot bootloader environment for flash images"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"
PV = "1.0+fslgit"

# if u-boot-flashenv not found in the supported targets, default to an empty env
FILESEXTRAPATHS:append := "${THISDIR}/${PN}/empty-flashenv:"

def uboot_env_get_uri_list(env_list):
    result = ""
    for file in env_list.split():
        result += "file://" + file + ".txt "

    return result

UBOOT_ENV_NAME ??= "u-boot-flashenv"
SRC_URI = " \
    ${@uboot_env_get_uri_list(d.getVar('UBOOT_ENV_NAME'))} \
"

DEFAULT_ENV:s32 ??= "u-boot-default-flashenv"
DEFAULT_ENV_PATH:s32 ??= "${STAGING_DIR_TARGET}${datadir}/env/"

# S32 boards have more default environments, e.g for QSPI and SDCARD
# Add also the SD environment and describe the mapping between the
# environment and the u-boot config (qspi, sdcard) as defined in the
# UBOOT_CONFIG variable
UBOOT_ENV_NAME:append:s32 = " u-boot-flashenv-sd"
UBOOT_ENV_NAME_MAP ??= ""
UBOOT_ENV_NAME_MAP:s32 = " \
	u-boot-flashenv    : qspi; \
	u-boot-flashenv-sd : sdcard; \
"

# We support includes if specified in the machine conf
UBOOT_ENV_INC_NAME ??= ""
SRC_URI:append = " ${@uboot_env_get_uri_list(d.getVar('UBOOT_ENV_INC_NAME'))}"

require u-boot-environment.inc

DEPENDS:append:s32 = "${@oe.utils.conditional("DEFAULT_ENV", "", "" , " u-boot-s32", d)}"
