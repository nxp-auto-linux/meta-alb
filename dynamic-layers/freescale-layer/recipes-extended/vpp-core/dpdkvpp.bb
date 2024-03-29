DESCRIPTION = "Data Plane Development Kit"
HOMEPAGE = "http://dpdk.org"
LICENSE = "BSD-3-Clause & LGPL-2.0-only & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://license/README;md5=3383def2d4c82237df281174e981a492"

SRC_URI = "git://github.com/nxp-qoriq/dpdk;nobranch=1 \
           file://add-RTE_KERNELDIR_OUT-to-split-kernel-bu.patch \
           file://0001-add-Wno-cast-function-type.patch \
           file://0001-Add-RTE_KERNELDIR_OUT.patch \
           file://0004-update-WERROR_FLAGS.patch \
"
SRCREV = "a36da6a94243015b228c15b8b9aa1e650fd4b96d"

RDEPENDS:${PN} += "python-subprocess"
DEPENDS = "virtual/kernel openssl"
DEPENDS:append:x86-64 = " numactl"
do_configure[depends] += "virtual/kernel:do_shared_workdir"

inherit module

COMPATIBLE_HOST = '(aarch64|arm|i.86|x86_64).*-linux'
COMPATIBLE_HOST:libc-musl = 'null'
COMPATIBLE_HOST:armv4 = 'null'
COMPATIBLE_HOST:armv5 = 'null'
COMPATIBLE_HOST:armv6 = 'null'

COMPATIBLE_MACHINE = "(imx|qoriq)"

DPDK_RTE_TARGET:x86-64 = "x86_64-native-linuxapp-gcc"
DPDK_RTE_TARGET:x86 = "i686-native-linuxapp-gcc"
DPDK_RTE_TARGET:armv7a = "${ARCH}-armv7a-linuxapp-gcc"
DPDK_RTE_TARGET:armv7ve = "${ARCH}-armv7a-linuxapp-gcc"
DPDK_RTE_TARGET ?= "${ARCH}-dpaa-linuxapp-gcc"

TLSDIALECT ?= ""
TLSDIALECT:aarch64 ?= "-ftls-model=local-dynamic"

export RTE_TARGET = "${DPDK_RTE_TARGET}"
export RTE_OUTPUT = "${S}/${RTE_TARGET}"

S = "${WORKDIR}/git"

EXTRA_OEMAKE += 'ETHTOOL_LIB_PATH="${S}/examples/ethtool/lib/${RTE_TARGET}" RTE_SDK="${S}" \
    OPENSSL_PATH="${STAGING_DIR_HOST}" RTE_KERNELDIR="${STAGING_KERNEL_DIR}" \
    RTE_KERNELDIR_OUT="${STAGING_KERNEL_BUILDDIR}" EXAMPLES_BUILD_DIR="${RTE_TARGET}" \
'

# Add -fcommon to CFLAGS to silence "multiple definition" errors
# due to gcc 10 setting -fno-common by default
TOOLCHAIN_OPTIONS += "-fcommon"

do_configure () {
	#############################################################
	### default value for prefix is "usr", unsetting it, so it
	### will not be concatenated in ${RTE_TARGET}/Makefile
	### which will cause compilation failure
	#############################################################
	unset prefix
	oe_runmake O=$RTE_TARGET T=$RTE_TARGET config
}

do_compile () {
	unset LDFLAGS TARGET_LDFLAGS BUILD_LDFLAGS

	cd ${S}/${RTE_TARGET}
	oe_runmake  CONFIG_RTE_EAL_IGB_UIO=n CONFIG_RTE_KNI_KMOD=y \
	            CONFIG_RTE_LIBRTE_PMD_OPENSSL=y \
                   EXTRA_LDFLAGS="-L${STAGING_LIBDIR} --hash-style=gnu" \
		   EXTRA_CFLAGS="${HOST_CC_ARCH} ${TOOLCHAIN_OPTIONS} -I${STAGING_INCDIR} -Ofast -fPIC ${TLSDIALECT}" \
		   CROSS="${TARGET_PREFIX}" \
		   prefix=""  LDFLAGS="${TUNE_LDARGS}"  WERROR_FLAGS="-w" V=1

	cd ${S}/examples/
        for APP in l2fwd l3fwd cmdif l2fwd-qdma l2fwd-crypto ipsec-secgw vhost kni ip_fragmentation ip_reassembly; do
            temp=`basename ${APP}`
            if [ ${temp} = "ipsec-secgw" ] || [ ${temp} = "l2fwd-crypto" ]; then
	        oe_runmake EXTRA_LDFLAGS="-L${STAGING_LIBDIR} --hash-style=gnu -fuse-ld=bfd" \
		       EXTRA_CFLAGS="${HOST_CC_ARCH} ${TOOLCHAIN_OPTIONS} -I${STAGING_INCDIR}" \
		       CROSS="${TARGET_PREFIX}" -C ${APP} CONFIG_RTE_LIBRTE_PMD_OPENSSL=y O="${S}/examples/${temp}"
            else
                oe_runmake EXTRA_LDFLAGS="-L${STAGING_LIBDIR} --hash-style=gnu -fuse-ld=bfd" \
                       EXTRA_CFLAGS="${HOST_CC_ARCH} ${TOOLCHAIN_OPTIONS} -I${STAGING_INCDIR}" \
                       CROSS="${TARGET_PREFIX}" -C ${APP} CONFIG_RTE_LIBRTE_PMD_OPENSSL=y O="${S}/examples/${temp}/"
            fi
        done

}

do_install () {
	oe_runmake O=${RTE_OUTPUT} T= install-runtime DESTDIR=${D}
	oe_runmake O=${RTE_OUTPUT} T= install-sdk DESTDIR=${D}

	# Install examples
        install -d 0644 ${D}/${datadir}/dpdk/cmdif/include
        install -d 0644 ${D}/${datadir}/dpdk/cmdif/lib
        cp examples/cmdif/lib/client/fsl_cmdif_client.h examples/cmdif/lib/server/fsl_cmdif_server.h \
            examples/cmdif/lib/shbp/fsl_shbp.h      ${D}/${datadir}/dpdk/cmdif/include
        cp examples/cmdif/lib/${RTE_TARGET}/librte_cmdif.a ${D}/${datadir}/dpdk/cmdif/lib
        install -d 0644 ${D}/${datadir}/dpdk/examples/ipsec_secgw
	cp -r ${S}/examples/ipsec-secgw/*.cfg  ${D}/${datadir}/dpdk/examples/ipsec_secgw
        cp -rf ${S}/nxp/* ${D}/${datadir}/dpdk
        
        # Remove the unneeded dir
        rm -rf ${D}/${datadir}/${RTE_TARGET}/app
}

PACKAGES += "${PN}-examples"

FILES:${PN}-dbg += " \
	${datadir}/dpdk/.debug \
	${datadir}/dpdk/examples/*/.debug \
	"
FILES:${PN}-staticdev += "${datadir}/dpdk/cmdif/lib/*.a \
"
FILES:${PN}-dev += " \
	${datadir}/dpdk/${RTE_TARGET}/.config \
	${includedir} \
	${includedir}/exec-env \
	${datadir}/dpdk/buildtools/ \
	${datadir}/dpdk/${RTE_TARGET}/include \
	${datadir}/dpdk/${RTE_TARGET}/lib \
	${datadir}/dpdk/mk \
	"

FILES:${PN} += " ${datadir}/ \
		 ${prefix}/sbin/ \
		 ${prefix}/bin/ \
		 ${libdir}/ \
		 "
FILES:${PN}-examples += " \
	${datadir}/examples/* \
	"
