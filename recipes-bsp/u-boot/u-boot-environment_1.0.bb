# I added this because there is no other way to generate the default
# flash environment for U-Boot properly
# <Heinz.Wrobel@nxp.com>
#
SUMMARY = "Create U-Boot bootloader environment for flash images"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"
PV = "1.0+fslgit"

# if u-boot-flashenv not found in the supported targets, default to an empty env
FILESEXTRAPATHS_append := "${THISDIR}/${PN}/empty-flashenv:"

def uboot_env_get_uri_list(env_list):
    result = ""
    for file in env_list.split():
        result += "file://" + file + ".txt "

    return result

UBOOT_ENV_NAME ??= "u-boot-flashenv"
SRC_URI = " \
    ${@uboot_env_get_uri_list(d.getVar('UBOOT_ENV_NAME', True))} \
"

DEFAULT_ENV_s32 ??= "u-boot-default-flashenv"
DEFAULT_ENV_PATH_s32 ??= "${STAGING_DIR_TARGET}${datadir}/env/"

# S32 boards have more default environments, e.g for QSPI and SDCARD
# Add also the SD environment and describe the mapping between the
# environment and the u-boot config (qspi, sdcard) as defined in the
# UBOOT_CONFIG variable
UBOOT_ENV_NAME_append_s32 = " u-boot-flashenv-sd"
UBOOT_ENV_NAME_MAP ??= ""
UBOOT_ENV_NAME_MAP_s32 = " \
	u-boot-flashenv    : qspi; \
	u-boot-flashenv-sd : sdcard; \
"

# Turns out that some board configs use a different size in U-Boot.
# If we do not want to override the board config with a different one,
# we have to be creative locally and second guess the environment size.
# I don't like this, but until I have a better solution, this is it.
UBOOT_ENV_IMAGE_SIZE_ls1043ardb = "131072 nand:8192 sdcard:8192"
UBOOT_ENV_IMAGE_SIZE_ls1043abluebox = "131072 nand:8192 sdcard:8192"

# For the factory image, we support a special environment
UBOOT_ENV_NAME_append_ls2084abbmini = " u-boot-flashenv-factory"

require u-boot-environment.inc

DEPENDS_append_s32 = "${@oe.utils.conditional("DEFAULT_ENV", "", "" , " u-boot-s32", d)}"
