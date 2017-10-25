require recipes-bsp/u-boot/u-boot.inc
inherit fsl-u-boot-localversion

DESCRIPTION = "U-boot bootloader"
HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"
SECTION = "bootloaders"
PROVIDES = "virtual/bootloader u-boot"
LICENSE = "GPLv2 & BSD-3-Clause & BSD-2-Clause & LGPL-2.0 & LGPL-2.1"
LIC_FILES_CHKSUM = " \
    file://Licenses/gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://Licenses/bsd-2-clause.txt;md5=6a31f076f5773aabd8ff86191ad6fdd5 \
    file://Licenses/bsd-3-clause.txt;md5=4a1190eac56a9db675d58ebe86eaf50c \
    file://Licenses/lgpl-2.0.txt;md5=5f30f0716dfdd0d91eb439ebec522ec2 \
    file://Licenses/lgpl-2.1.txt;md5=4fbd65380cdd255951079008b364516c \
"
PV = "2016.01"
INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "libgcc virtual/${TARGET_PREFIX}gcc dtc-native openssl openssl-native "

DEFAULT_PREFERENCE = "-1"

SCMVERSION = "y"
LOCALVERSION = ""

SRC_URI = "git://git.freescale.com/auto/u-boot.git;protocol=git;branch=master"
#SRC_URI = "git://sw-stash.freescale.net/scm/alb/u-boot.git;protocol=http;branch=develop"
#SRCREV = "${AUTOREV}"
#SRC_URI = "git://sw-stash.freescale.net/scm/alb/u-boot.git;protocol=http;branch=develop;tag=efe143d62a0f7eabca968bf4cc340f4d96938e37"

# bsp15.0
SRCREV = "5c2fb7f29dd3988666ec9752cae444ea1bb3ae17"

inherit cml1

EXTRA_OEMAKE += 'HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}"'

do_configure () {
	oe_runmake ${UBOOT_MACHINE}
}

do_compile () {
	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', 'ld-is-gold', '', d)}" = "ld-is-gold" ] ; then
		sed -i 's/$(CROSS_COMPILE)ld$/$(CROSS_COMPILE)ld.bfd/g' config.mk
	fi

	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS

	oe_runmake all
}


# This hack is required so that a fsl-image-full finds a package "u-boot-images". Unfortunately,
# bitbake apparently can't alias package name references.
PACKAGES = " u-boot-images "
FILES_u-boot-images = "${FILES_${PN}}"

