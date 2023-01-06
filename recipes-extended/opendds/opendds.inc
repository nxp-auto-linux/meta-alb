SUMMARY = "OpenDDS is an open source C++ implementation of the Object Management Group (OMG) Data Distribution Service (DDS)"
HOMEPAGE = "http://www.opendds.org"
SECTION = "devel"
LICENSE = "OpenDDS"
LIC_FILES_CHKSUM = "file://LICENSE;md5=11ee76f6fb51f69658b5bb8327c50b11"

inherit autotools

S = "${WORKDIR}/git"
B = "${S}"

ACE_TAO_ARCHIVE = "ACE+TAO-2.2a_with_latest_patches_NO_makefiles.tar.gz"

DDS_SRC_BRANCH ?= "master"
SRC_URI = "git://github.com/objectcomputing/OpenDDS.git;protocol=https;branch=${DDS_SRC_BRANCH} \
		http://download.ociweb.com/TAO-2.2a/${ACE_TAO_ARCHIVE};name=tao; \
		"

SRC_URI[tao.sha256sum] = "3ea3c9c019ba9ef1f6de99d05d373e09ffb881d7de56faa3105a2db39583ae20"

DEPENDS += "perl"
RDEPENDS:${PN}-dev += "perl"

# For the Yocto based build, we need to work around some issues with
# the special build system of OpenDDS. We want to appear as if we
# are a native host and specify our cross compiler by different means.
# The wrapper hack is needed because the OpenDDS build system does not
# like spaces in the compiler name specification and we therefore
# could not state our sysroot otherwise.
CROSSWRAPPERHACK = "${WORKDIR}/${HOST_PREFIX}wrap-"
export CC = "${CC_FOR_BUILD}"
export CXX = "${CXX_FOR_BUILD}"
CONFIGUREOPTS = " --target=linux-cross \
		  --target-compiler=${CROSSWRAPPERHACK}gcc \
		  --prefix=${prefix} \
		  --no-tests \
		  --verbose \
"
# wget ${ACE_TAO_ARCHIVE} from configure script fails for no
# apparent reason, so we add it to SRC_URI and tell ./configure
# how to get the files inside.
CONFIGUREOPTS += " \
	--ace "${B}/../ACE_wrappers" \
	--tao "${B}/../ACE_wrappers/TAO" \
	"

DISABLE_STATIC = ""
DEBUG_PREFIX_MAP = ""

do_configure:prepend () {
	# NOTE: You can't configure this recipe twice with different
	# settings without cleaning first. The build system of OpenDDS
	# conditionally configures if it find configurations already!

	# The OpenDDS build system requires us to be there
	cd "${S}"
}

do_configure() {
    ./configure ${CONFIGUREOPTS}
}

write_wrapperhack () {
	echo > "${CROSSWRAPPERHACK}$1" "#!/bin/sh"
	echo >>"${CROSSWRAPPERHACK}$1" "${HOST_PREFIX}$1 ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS} \$@"
	chmod +x "${CROSSWRAPPERHACK}$1"
}

do_compile:prepend () {
	write_wrapperhack gcc
	write_wrapperhack g++
}


do_install:prepend () {
	# We are only interested in the cross compiled output
	cd "${B}/build/target"
}

do_install:append () {
	rm ${D}${datadir}/dds/dds/Version.h
    cp ${D}${includedir}/dds/Version.h ${D}${datadir}/dds/dds
}

FILES:${PN} = " \
    ${libdir} \
    ${bindir} \
"

FILES:${PN}-dev += " \
    ${libdir}/cmake/OpenDDS/* \
    ${includedir} \
    ${datadir} \
"

BBCLASSEXTEND = "native nativesdk"
