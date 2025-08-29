
inherit fsl-eula-unpack

# `fsl-auto-eula-unpack.bbclass` overrides behavior from `fsl-eula-unpack.bbclass`.
#
# Its main purpose is to disable the `fsl_bin_do_unpack()` function defined in 
# `fsl-eula-unpack.bbclass`, as the unpacking logic provided there is not needed in 
# our context.
#
# Additionally, `fsl-auto-eula-unpack.bbclass` is used to configure license-related variables
# to ensure proper integration with the build system.

FSL_EULA_FILE_MD5SUM_LA_OPT_NXP_SOFTWARE_LICENSE_V63 = "04de690020b04714cd7cd6fb8f0b2fd9"
FSL_EULA_FILE_MD5SUMS:append = "${FSL_EULA_FILE_MD5SUM_LA_OPT_NXP_SOFTWARE_LICENSE_V63}"

FSL_EULA_FILE_MD5SUM = "${FSL_EULA_FILE_MD5SUM_LA_OPT_NXP_SOFTWARE_LICENSE_V63}"

python fsl_bin_do_unpack() {
    return;
}


