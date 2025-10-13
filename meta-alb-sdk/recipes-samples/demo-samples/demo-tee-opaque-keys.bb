# Copyright 2024 NXP

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.BSD;md5=d1fe458e57ae72e9abc9aff2684690d0"

URL ?= "git://github.com/nxp-auto-linux/alb-demos;protocol=https"
BRANCH ?= "${RELEASE_BASE}"
SRC_URI = "${URL};branch=${BRANCH}"

S = "${WORKDIR}/git"
SRCREV = "6a3f6705a5a25158d9c81d397734765a2ee5187d"

CA_DESTDIR= "${D}${bindir}"
TA_DESTDIR = "${D}${nonarch_base_libdir}/optee_armtz"

do_install() {
	install -d ${TA_DESTDIR}
	install -d ${CA_DESTDIR}
        oe_runmake install INSTALLDIR=${CA_DESTDIR} TA_INSTALLDIR=${TA_DESTDIR}
}

FILES:${PN} = "${bindir} \
	       ${nonarch_base_libdir}/optee_armtz/ \
               "

DEPENDS = "optee-client optee-os-tadevkit python3-cryptography-native"

inherit python3native

export OPENSSL_MODULES="${STAGING_LIBDIR_NATIVE}/ossl-modules"

EXTRA_OEMAKE += "TA_DEV_KIT_DIR=${STAGING_INCDIR}/optee/export-user_ta \
		 TEEC_EXPORT=${STAGING_DIR_HOST}${prefix} \
		 LIBGCC_LOCATE_CFLAGS='${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}' \
		 CROSS_COMPILE=${TARGET_PREFIX} \
		 SYSROOT=${STAGING_DIR_TARGET} \
		 samples=tee_opaque_keys \
		"
