# This recipe builds a variant of the normal virtual/kernel.
# By applying a "disabled.cfg" we can build a reduced size kernel
# that enables us to put together a recovery linux properly for smaller
# flash sizes where the full featured kernel does not fit.
# This is used for the ITB recovery images.
#
# This recipe MUST NOT patch kernel sources, but it MAY modify the
# configuration with fragments.
# Out of tree modules are not supported with this kind of kernel.
# <Heinz.Wrobel@nxp.com>

# The mechanism is:
# - Preserve any recipe and class based variables we may need later
# - Use externalsrc.bbclass as baseline approach. It won't do all
#   that we need to inherit sources and config from virtual/kernel
# - Restore variables needed for kernel.bbclass
# - Reuse the original virtual/kernel recipe to set the environment
#   in our context and our package name.
# - Then make virtual/kernel put together sources and base configuration
#   and refer to that source and rebuild our config based on that config

inherit varflagsremove

# Magic to get back the version of the caller. The caller needs to
# do RECIPEV := "${PV}" before including this!
# We don't directly do a PV change in the caller because we may want
# to change the implementation here without changing the caller
PV := "${RECIPEV}"

PRESERVE_ORIGINAL_VALUES = "S B WORKDIR SRCPV STAGING_KERNEL_DIR STAGING_KERNEL_BUILDDIR"
RESTORE_PRE_KERNEL_VALUES = "S B WORKDIR SRCPV"
RESTORE_POST_KERNEL_ORIGINAL_VALUES = "STAGING_KERNEL_DIR"

python () {
        # externalsrc.bbclass overwrites S, but we want to use the
        # S that kernel.bbclass intends to operate on. So as very
        # first anynonmous function we preserve the default variables.
        pv = d.getVar("PRESERVE_ORIGINAL_VALUES") or ""
        for i in pv.split():
                ov = d.getVar(i)
                d.setVar("ORIGINAL_" + i, ov)
}
inherit externalsrc

python () {
        pv = d.getVar("RESTORE_PRE_KERNEL_VALUES") or ""
        for i in pv.split():
                ov = d.getVar("ORIGINAL_" + i)
                d.setVar(i, ov)
}

# Our flash kernel needs to have different package names so that
# we do not conflict with the normal virtual/kernel used for
# bigger images
KERNEL_PACKAGE_NAME = "kernel-flash"

# Set the environment to the virtual/kernel equivalent
BASERECIPE = "${PREFERRED_PROVIDER_virtual/kernel}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${BASERECIPE}:"
require recipes-kernel/linux/${BASERECIPE}_${RECIPEV}.bb

#----------------------------------------------------------------------
# Before we do anything in this recipe, we need to ensure that
# we have the source from the full kernel as we will share it.
# This means that we need to run a subset of the virtual/kernel
# recipe only to the point where we have the source patched properly

# patch up kernel.bbclass
VARFLAGSREMOVE += "do_clean[depends]:remove=make-mod-scripts:do_clean"
do_clean[depends] += "make-mod-scripts-flash:do_clean"

# This is not the default kernel! We just have a recipe that happens
# to build a kernel to be used for special things.
PROVIDES:remove = "virtual/kernel"

# Now that all variables are preset "as if" for the virtual/kernel
# we second guess kernel.bbclass to reuse the sources but build
# separately. We do this after the original recipe code to ensure
# that this python functions runs last.
python () {
        pv = d.getVar("RESTORE_POST_KERNEL_ORIGINAL_VALUES") or ""
        for i in pv.split():
                ov = d.getVar("ORIGINAL_" + i)
                d.setVar(i, ov)

        # Given that we need to have a custom make-mod-scripts that
        # we need share data with, we have to stay in the work-shared.
        # We cannot however use the same as for virtual/kernel.
        # The following is really what the variable should be like.
        d.setVar("STAGING_KERNEL_BUILDDIR", "${TMPDIR}/work-shared/${MACHINE}/${KERNEL_PACKAGE_NAME}-build-artifacts")
}

# Just reference the source tree that was created by virtual/kernel
EXTERNALSRC = "${ORIGINAL_STAGING_KERNEL_DIR}"
EXTERNALSRC_SYMLINKS = ""

# We don't do the kernel.bbclass thing as that would be
# too late when using the external source
python do_symlink_kernsrc () {
}

python do_symlink_extkernsrc () {
    s = d.getVar("S")
    if s[-1] == '/':
        # drop trailing slash, so that os.symlink(kernsrc, s) doesn't use s as directory name and fail
        s=s[:-1]
    kernsrc = d.getVar("STAGING_KERNEL_DIR")
    if s != kernsrc and not os.path.islink(s):
        os.symlink(kernsrc, s)
}

addtask symlink_extkernsrc before do_unpack do_configure do_merge_delta_config do_populate_lic
do_symlink_extkernsrc[depends] += "virtual/kernel:do_shared_workdir"

# When we pick up the sources as base for our custom kernel, we also
# need to pick up the config from virtual/kernel as base for our
# modifications. As we depend on virtual/kernel:do_shared_workdir for
# the non-running do_patch, we can be sure this .config exists.
# Our do_merge_delta config simply falls back on that configuration
# instead of building it locally.
do_merge_delta_config() {
    cp "${ORIGINAL_STAGING_KERNEL_BUILDDIR}/.config" .config

    # check if bigendian is enabled
    if [ "${SITEINFO_ENDIANNESS}" = "be" ]; then
        echo "CONFIG_CPU_BIG_ENDIAN=y" >> .config
        echo "CONFIG_MTD_CFI_BE_BYTE_SWAP=y" >> .config
    fi

    # add config fragments to reduce the given config
    for deltacfg in ${DELTA_KERNEL_REDUCE_DEFCONFIG}; do
        if [ -f ${S}/arch/${ARCH}/configs/${deltacfg} ]; then
            oe_runmake -C ${S} O=${B} ${deltacfg}
        elif [ -f "${WORKDIR}/${deltacfg}" ]; then
            ${S}/scripts/kconfig/merge_config.sh -m .config ${WORKDIR}/${deltacfg}
        elif [ -f "${deltacfg}" ]; then
            ${S}/scripts/kconfig/merge_config.sh -m .config ${deltacfg}
        fi
    done

    cp .config ${WORKDIR}/defconfig
}
addtask merge_delta_config after do_unpack do_symlink_kernsrc

# Now we reduce the kernel as much as we can by reducing the already
# existing configuration. We reduce in a kernel version specific way
# because the configs change all the time.
SRC_URI:append = " \
    file://disabled_${RECIPEV}.cfg \
"
DELTA_KERNEL_REDUCE_DEFCONFIG = "disabled_${RECIPEV}.cfg"
