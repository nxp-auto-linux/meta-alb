# This overrides the patch integrated into SDK 2.0, which isn't accurate for SDK 2.0.

# The original recipe hardcodes "/lib" rather than using the proper nonarch_base_libdir
FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

# This patch is broken for SDK 2.0
SRC_URI_remove = "file://base_libdir.patch"

SRC_URI += " \
    file://nonarch_base_libdir.patch \
"

