
# Assign the external toolchain if no other value is already assigned (default assignment)
# Any user's setting (using '=') will overide this default assignment
# On the other hand is safe to set the default value as the toolchain selection
# is decided first based on the TCMODE and TCLIBC. If no external toolchain is set
# (e.g. TCMODE="default"), this default assignment will be ignored
EXTERNAL_TOOLCHAIN ?= "${TOPDIR}/tmp/ext-toolchain/usr"

# Default assignment: as above, user setting will override this default asignment
# TCMODE is needed before an event handler or anonymous python function is called
TCMODE ?= "${@toolchain_get_tcmode(d)}"


# Default assignment: as above, user setting will override this default asignment
# TCLIB is needed before an event handler or anonymous python function is called
TCLIBC ?= "${@toolchain_get_tclibc(d)}"

# Overall, the toolchain selection is done based on the following algorithm:
#
#  * if the user set (in local.conf) the external toolchain: use this
#    toolchain accordingly to the user's settings
#
#  * else, if the Ubuntu cross toolchain is installed in standard
#    location (/usr/<arch>-linux-gnu), use this as external toolchain
#
#  * else, use the standard Yocto toolchain



ELT_TARGET_SYS ?= "${TARGET_ARCH}-linux-gnu"
BASE_EXT_TC ?= "/usr"

myshell_install_ext_toolchain() {

	# It is assumed that the Ubuntu toolchain is installed on the Host
	# The user should install the required packages, as below:
	#
	# sudo apt-get install gcc-aarch64-linux-gnu
	# sudo apt-get install g++-aarch64-linux-gnu
	# sudo apt-get install libc6-dev-arm64-cross
	# These commands will install the toolchain in /usr - se BASE_EXT_TC

	# In order to be shaped like a standard linaro toolchain we do some
	# preparation: create dummy file, create symlinks
	# This is done in  ${EXTERNAL_TOOLCHAIN} to avoid changing the system
	# /usr folder

	# TODO:
	# This is a temporary solution. The target is to install the required
	# packages in a fakeroot.


	if ! [ -d ${EXTERNAL_TOOLCHAIN} ]; then
		/usr/bin/install -d ${EXTERNAL_TOOLCHAIN}
	fi

	if ! [ -d ${EXTERNAL_TOOLCHAIN}/${ELT_TARGET_SYS} ]; then
		/usr/bin/install -d ${EXTERNAL_TOOLCHAIN}/${ELT_TARGET_SYS}
		/bin/ln -s ${BASE_EXT_TC}/${ELT_TARGET_SYS}/bin ${EXTERNAL_TOOLCHAIN}/${ELT_TARGET_SYS}/bin
		/bin/ln -s ${BASE_EXT_TC}/${ELT_TARGET_SYS}/include ${EXTERNAL_TOOLCHAIN}/${ELT_TARGET_SYS}/include
		/bin/ln -s ${BASE_EXT_TC}/${ELT_TARGET_SYS}/lib ${EXTERNAL_TOOLCHAIN}/${ELT_TARGET_SYS}/lib

	fi

	if ! [ -d ${EXTERNAL_TOOLCHAIN}/bin ]; then
		/usr/bin/install -d ${EXTERNAL_TOOLCHAIN}/bin
		/bin/ln -s ${BASE_EXT_TC}/bin/${ELT_TARGET_SYS}-* ${EXTERNAL_TOOLCHAIN}/bin/
	fi

	if ! [ -d ${EXTERNAL_TOOLCHAIN}/${ELT_TARGET_SYS}/libc/usr ]; then
		/usr/bin/install -d ${EXTERNAL_TOOLCHAIN}/${ELT_TARGET_SYS}/libc/usr
	fi

	if ! [ -d ${EXTERNAL_TOOLCHAIN}/${ELT_TARGET_SYS}/libc/usr/lib ]; then
		/bin/ln -s ${EXTERNAL_TOOLCHAIN}/${ELT_TARGET_SYS}/lib ${EXTERNAL_TOOLCHAIN}/${ELT_TARGET_SYS}/libc/usr/lib
	fi

	if ! [ -d ${EXTERNAL_TOOLCHAIN}/${ELT_TARGET_SYS}/libc/usr/include ]; then
		/bin/ln -s ${EXTERNAL_TOOLCHAIN}/${ELT_TARGET_SYS}/include ${EXTERNAL_TOOLCHAIN}/${ELT_TARGET_SYS}/libc/usr/include
	fi

	if ! [ -d ${EXTERNAL_TOOLCHAIN}/${ELT_TARGET_SYS}/libc/usr/share ]; then
		/usr/bin/install -d ${EXTERNAL_TOOLCHAIN}/${ELT_TARGET_SYS}/libc/usr/share
		/usr/bin/touch ${EXTERNAL_TOOLCHAIN}/${ELT_TARGET_SYS}/libc/usr/share/empty
	fi
}


def ubuntu_ext_toolchain(d):
    arch = d.getVar("TARGET_ARCH", True)
    base = d.getVar("BASE_EXT_TC", True)

    elt_target =  "%s-linux-gnu" % arch
    host_tc ="%s/%s" % (base, elt_target)
    host_gcc = "%s/bin/%s-gcc" % (base, elt_target)

    if os.path.exists(host_tc) and os.path.exists(host_gcc):
        # Ubuntu host cross toolchain is installed on the host
        # bb.note("Using Ubuntu host cross toolchain %s" % host_gcc)
        return True

    return False

# Evaluated a few times only initially and then self holding through
# the shadow variable, so not really performance critical
def toolchain_get_tcmode(d):
    tcmode = d.getVar("TCMODESHADOW", True)
    if tcmode:
        return tcmode

    tcmode = "default"
    if ubuntu_ext_toolchain(d) is True:
        tcmode = "external-linaro"
    bb.debug(1, "Dynamic toolchain selection for TCMODE selected %s" % tcmode)
    d.setVar("TCMODESHADOW", tcmode)
    return tcmode

# Evaluated a few times only initially and then self holding through
# the shadow variable, so not really performance critical
def toolchain_get_tclibc(d):
    tclibc = d.getVar("TCLIBCSHADOW", True)
    if tclibc:
        return tclibc

    tclibc = "glibc"
    if ubuntu_ext_toolchain(d) is True:
        tclibc = "external-linaro-toolchain"
    bb.debug(1, "Dynamic toolchain selection for TCLIBC selected %s" % tclibc)
    d.setVar("TCLIBCSHADOW", tclibc)
    return tclibc


# In case the Ubuntu toolchain is used, this event handler creates a wrapper
# for the the host toolchain, to shape it like a linaro-external-toolchain
# (e.g. toolchain install)
# The toolchain install is done in the ConfigParsed event handler, as this
# event is triggered before the toolchain is actually being used.
# This could have been done also in the ubuntu_ext_toolchain, but because that 
# function is called for every TCMODE/TCLIBC access, it is preffered to keep 
# it with minimal overhead

addhandler ubuntu_toolchain_setup
ubuntu_toolchain_setup[eventmask] = "bb.event.ConfigParsed"
python ubuntu_toolchain_setup () {
    d = e.data

    if ubuntu_ext_toolchain(d) is True:
        bb.build.exec_func("myshell_install_ext_toolchain", d)

    return
}

