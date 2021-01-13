FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# Add the patch from meta-freescale with the fix for building with GCC 10
SRC_URI += " \
    file://0001-optee-os-fix-gcc10-compilation-issue-and-missing-cc-.patch \
"
