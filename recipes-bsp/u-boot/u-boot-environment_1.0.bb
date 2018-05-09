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

SRC_URI = "\
        file://u-boot-flashenv.txt \
"
UBOOT_ENV_NAME = "u-boot-flashenv"

# Turns out that some board configs use a different size in U-Boot.
# If we do not want to override the board config with a different one,
# we have to be creative locally and second guess the environment size.
# I don't like this, but until I have a better solution, this is it.
UBOOT_ENV_IMAGE_SIZE_ls1043ardb = "131072 nand:8192 sdcard:8192"
UBOOT_ENV_IMAGE_SIZE_ls1043abluebox = "131072 nand:8192 sdcard:8192"
UBOOT_ENV_IMAGE_SIZE_ls1012a = "262144"

# For the factory image, we support a special environment
UBOOT_ENV_NAME_append_ls2084abbmini = " u-boot-flashenv-factory"
SRC_URI_append_ls2084abbmini = " \
    file://u-boot-flashenv-factory.txt \
"

require u-boot-environment.inc
