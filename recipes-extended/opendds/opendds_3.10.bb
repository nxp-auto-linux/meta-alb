SUMMARY = "OpenDDS is an open source C++ implementation of the Object Management Group (OMG) Data Distribution Service (DDS)"
HOMEPAGE = "http://www.opendds.org"
SECTION = "devel"
LICENSE = "OpenDDS"
LIC_FILES_CHKSUM = "file://LICENSE;md5=11ee76f6fb51f69658b5bb8327c50b11"

inherit autotools

S = "${WORKDIR}/OpenDDS-${PV}"
B = "${S}"

SRC_URI = "https://github.com/objectcomputing/OpenDDS/releases/download/DDS-3.10/OpenDDS-3.10.tar.gz \
           file://0001-opendds-Make-Version.h-symlink-path-relative.patch \
           "
SRC_URI[md5sum] = "476572bdeb034f8d60b7490ce4595dd9"
SRC_URI[sha256sum] = "824e900898e5d75f2875ead041bbef654a7a00cb59029f353df868e7fabff50f"

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
		  --verbose \
"

DISABLE_STATIC = ""

do_configure_prepend () {
	# NOTE: You can't configure this recipe twice with different
	# settings without cleaning first. The build system of OpenDDS
	# conditionally configures if it find configurations already!

	# The OpenDDS build system requires us to be there
	cd "${S}"
}

write_wrapperhack () {
	echo > "${CROSSWRAPPERHACK}$1" "#!/bin/sh"
	echo >>"${CROSSWRAPPERHACK}$1" "${HOST_PREFIX}$1 ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS} \$@"
	chmod +x "${CROSSWRAPPERHACK}$1"
}

do_compile_prepend () {
	write_wrapperhack gcc
	write_wrapperhack g++
}


do_install_prepend () {
	# We are only interested in the cross compiled output
	cd "${B}/build/target"
}
