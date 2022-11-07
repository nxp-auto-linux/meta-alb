
SRCREV = "58d1c0ca1c74986507773d50cce88fa1c8afb435"
DDS_SRC_BRANCH = "branch-DDS-3.14"

require opendds.inc

do_install:append:class-native() {
	# In some cases the binaries are not installed correctly. Check and fix.
	if [ ! -e ${D}${bindir}/opendds_idl ]; then
		cp -Lf ${S}/build/host/dds/idl/opendds_idl ${D}${bindir}/opendds_idl
	fi
	if [ ! -e ${D}${bindir}/ace_gperf ]; then
		cp -Lf ${WORKDIR}/ACE_wrappers/build/host/bin/ace_gperf ${D}${bindir}/ace_gperf
	fi
	if [ ! -e ${D}${bindir}/tao_idl ]; then
		cp -Lf ${WORKDIR}/ACE_wrappers/build/host/TAO/TAO_IDL/tao_idl ${D}${bindir}/tao_idl
	fi

	# Prepare HOST_ROOT expected by DDS for target build
	mkdir -p ${D}${bindir}/DDS_HOST_ROOT/ACE_wrappers/bin
	mkdir -p ${D}${bindir}/DDS_HOST_ROOT/bin
	ln -sr ${D}${bindir}/opendds_idl ${D}${bindir}/DDS_HOST_ROOT/bin/opendds_idl
	ln -sr ${D}${bindir}/ace_gperf ${D}${bindir}/DDS_HOST_ROOT/ACE_wrappers/bin/ace_gperf
	ln -sr ${D}${bindir}/tao_idl ${D}${bindir}/DDS_HOST_ROOT/ACE_wrappers/bin/tao_idl
}
